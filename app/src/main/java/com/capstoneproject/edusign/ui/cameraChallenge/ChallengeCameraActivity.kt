package com.capstoneproject.edusign.ui.cameraChallenge

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.data.db.QuestionEntity
import com.capstoneproject.edusign.data.model.Prediction
import com.capstoneproject.edusign.databinding.ActivityChallengeCameraBinding
import com.capstoneproject.edusign.ui.detailChallenge.DetailChallengeActivity
import com.capstoneproject.edusign.ui.resultPage.ResultTranslateVideoService
import com.capstoneproject.edusign.ui.resultPage.ResultTranslateViewModel
import com.capstoneproject.edusign.util.CameraResultViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class ChallengeCameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityChallengeCameraBinding

    // ini buat kamera
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var timeRemaining: Long = 0
    private val maxTime = 3000
    private val delayInMillis = 5000
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var isRecording = false
    private var videoUri: Uri? = null

    // ini buat result predictionnya
    private lateinit var viewModel: ResultTranslateViewModel
    private lateinit var videoView: VideoView
    private lateinit var videoPlaybackServiceIntent: Intent
    private var currentPosition: Int = 0

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityChallengeCameraBinding.inflate(layoutInflater)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContentView(viewBinding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Camera
        /// Assign the videoUri value from intent
        val videoUriString = intent.getStringExtra("videoUri")
        if (videoUriString != null) {
            videoUri = Uri.parse(videoUriString)
        } else {
            // Handle the case where videoUriString is null
            Log.e(TAG, "Video Uri String is null.")
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        countDownTimer = object : CountDownTimer(maxTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished / 1000

            }

            override fun onFinish() {
                captureVideo()
            }
        }

        viewBinding.videoCaptureButton.setOnClickListener{
            viewBinding.timerText.visibility = View.VISIBLE
            viewBinding.videoCaptureButton.isEnabled = false
            captureVideo()
        }

        val restartMainActivity = intent.getBooleanExtra("restartMainActivity", false)
        if (restartMainActivity) {
            viewBinding.videoCaptureButton.setBackgroundColor(Color.BLUE)
            viewBinding.timerText.visibility = View.GONE
        }

        // Video View of the video you just record
        videoView = viewBinding.previewVideo

        val cameraResultViewModelFactory = CameraResultViewModelFactory(application)
        viewModel = ViewModelProvider(this, cameraResultViewModelFactory)[ResultTranslateViewModel::class.java]
    }


    // untuk kamera
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.LOWEST))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()


            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }


            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {

                cameraProvider.unbindAll()


                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        val curRecording = recording
        if (curRecording != null) {

            curRecording.stop()
            recording = null
            return
        }


        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()


        val countDownTimer = object : CountDownTimer(delayInMillis.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                viewBinding.timerText.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                viewBinding.timerText.text = "Start Movement"
                recording = videoCapture.output
                    .prepareRecording(this@ChallengeCameraActivity, mediaStoreOutputOptions)
                    .start(ContextCompat.getMainExecutor(this@ChallengeCameraActivity)) { recordEvent ->
                        when (recordEvent) {
                            is VideoRecordEvent.Start -> {
                                viewBinding.videoCaptureButton.apply {
                                    text = "Recording"
                                    setBackgroundColor(Color.RED)
                                    isRecording = true
                                    startTimer()
                                    hideNavigationBar()
                                }
                            }
                            is VideoRecordEvent.Finalize -> {
                                if (!recordEvent.hasError()) {
                                    videoUri = recordEvent.outputResults.outputUri
                                    val msg = "Video capture succeeded: " +
                                            "$videoUri"
                                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, msg)
                                    viewBinding.videoCaptureButton.apply {
                                        val buttonColor = getColor(R.color.blue_primary)
                                        text = "Start"
                                        setBackgroundColor(buttonColor)
                                    }
                                    videoUri?.let { viewModel.performPrediction(it) }
                                    viewModel.predictionLiveData.observe(this@ChallengeCameraActivity) { prediction ->
                                        handlePredictionResult(prediction, intent)
                                    }
                                    viewModel.errorLiveData.observe(this@ChallengeCameraActivity) { errorMessage ->
                                        handleErrorMessage(errorMessage)
                                    }
                                    viewModel.loadingLiveData.observe(this@ChallengeCameraActivity) { isLoading ->
                                        if (isLoading) {
                                            viewBinding.progressBar.visibility = View.VISIBLE
                                            viewBinding.translateResult.visibility = View.INVISIBLE
                                        } else {
                                            viewBinding.progressBar.visibility = View.GONE
                                            viewBinding.translateResult.visibility = View.VISIBLE
                                        }
                                    }

                                    videoView.setVideoURI(videoUri)
                                    videoView.setOnPreparedListener { mediaPlayer ->
                                        mediaPlayer.setOnVideoSizeChangedListener { _, _, _ ->

                                            val videoWidth = mediaPlayer.videoWidth
                                            val videoHeight = mediaPlayer.videoHeight
                                            val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()


                                            val screenWidth = videoView.width
                                            val screenHeight = videoView.height
                                            val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()


                                            val videoParams = videoView.layoutParams
                                            if (videoProportion > screenProportion) {
                                                videoParams.width = screenWidth
                                                videoParams.height = (screenWidth / videoProportion).toInt()
                                            } else {
                                                videoParams.width = (videoProportion * screenHeight).toInt()
                                                videoParams.height = screenHeight
                                            }
                                            videoView.layoutParams = videoParams
                                        }
                                        mediaPlayer.setOnCompletionListener { mediaPlayer ->
                                            mediaPlayer.start()
                                            mediaPlayer.isLooping = true
                                        }
                                        mediaPlayer.seekTo(currentPosition)
                                        mediaPlayer.start()
                                    }
                                    videoPlaybackServiceIntent = Intent(this@ChallengeCameraActivity, ResultTranslateVideoService::class.java)
                                    videoPlaybackServiceIntent.putExtra("videoUri", videoUri?.toString()) // Convert Uri to String
                                    startService(videoPlaybackServiceIntent)

                                    viewBinding.apply {
                                        timerText.visibility = View.GONE
                                        viewFinder.visibility = View.GONE
                                        videoView.visibility = View.GONE
                                        videoCaptureButton.visibility = View.GONE

                                        linearLayout.visibility = View.VISIBLE
                                        previewVideo.visibility = View.VISIBLE
                                        linearLayoutHasilTest.visibility = View.VISIBLE
                                        hasilTest.visibility = View.VISIBLE
                                    }
                                } else {
                                    recording?.close()
                                    recording = null
                                    Log.e(
                                        TAG, "Video capture ends with error: " +
                                                "${recordEvent.error}"
                                    )
                                }
                                viewBinding.videoCaptureButton.apply {
                                    text = "Start"
                                    isEnabled = true
                                }
                                isTimerRunning = false
                            }
                        }
                    }
            }
        }
        countDownTimer.start()
    }

    private fun hideNavigationBar() {
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    private fun startTimer() {
        if (!isTimerRunning) {
            countDownTimer.start()
            isTimerRunning = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    // untuk result prediction
    override fun onPause() {
        super.onPause()
        if (videoView.isPlaying) {
            videoView.pause()
            currentPosition = videoView.currentPosition
        }
    }

    override fun onResume() {
        super.onResume()
        if (!videoView.isPlaying) {
            videoView.seekTo(currentPosition)
            videoView.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(videoPlaybackServiceIntent)
        videoView.stopPlayback()
    }

    override fun onBackPressed() {
        if (videoUri != null) {
            val contentResolver: ContentResolver = contentResolver
            contentResolver.delete(videoUri!!, null, null)
        }

        val intent = Intent(this, DetailChallengeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun handlePredictionResult(prediction: Prediction, intent: Intent) {

        val predictionsList = prediction.prediction
        val firstPrediction = predictionsList.firstOrNull() ?: ""
        val formattedResult = "$firstPrediction"
        val data = Intent()

        viewBinding.translateResult.text = formattedResult

        var receivedQuestion: QuestionEntity?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
             receivedQuestion = intent.getParcelableExtra(KATA_EXTRA, QuestionEntity::class.java)
        } else {
             receivedQuestion = intent.getParcelableExtra<QuestionEntity>(KATA_EXTRA)
        }

        if (receivedQuestion != null) {
            if (formattedResult == receivedQuestion.kata.lowercase()) {
                viewBinding.benarShow.visibility = View.VISIBLE
                data.putExtra(DetailChallengeActivity.RESULT_ANS_EXTRA, true)
            } else {
                viewBinding.salahShow.visibility = View.VISIBLE
                data.putExtra(DetailChallengeActivity.RESULT_ANS_EXTRA, false)
            }
        }
        data.putExtra(DetailChallengeActivity.VALIDATE_ANS_EXTRA, receivedQuestion!!.id)
        setResult(Activity.RESULT_OK, data)

        viewBinding.btnBackToSoal.setOnClickListener {
            if (videoUri != null) {
                val contentResolver: ContentResolver = contentResolver
                contentResolver.delete(videoUri!!, null, null)
            }
            finish()
        }
    }

    private fun handleErrorMessage(error: String) {
        val errorMessage = "Prediction failed: $error"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val KATA_EXTRA = "kataextra"
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}