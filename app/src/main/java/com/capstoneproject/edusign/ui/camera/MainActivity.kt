package com.capstoneproject.edusign.ui.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
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
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.databinding.ActivityMainBinding
import com.capstoneproject.edusign.ui.resultPage.ResultTranslateActivity
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var timeRemaining: Long = 0
    private val maxTime = 3000
    private val delayInMillis = 5000 // Adjust the delay time as needed (in milliseconds)

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(viewBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
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

        viewBinding.videoCaptureButton.setOnClickListener {
            viewBinding.timerText.visibility = View.VISIBLE
            viewBinding.videoCaptureButton.isEnabled = false
            captureVideo()
        }

        val restartMainActivity = intent.getBooleanExtra("restartMainActivity", false)
        if (restartMainActivity) {
            // Reset the button color and hide the start movement text
            viewBinding.videoCaptureButton.setBackgroundColor(Color.BLUE)
            viewBinding.timerText.visibility = View.GONE
        }


    }

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
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
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
            // Stop the current recording session.
            curRecording.stop()
            recording = null
            return
        }

        // create and start a new recording session
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


        val countDownTimer = object: CountDownTimer(delayInMillis.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                viewBinding.timerText.text = secondsRemaining.toString()
            }

            override fun onFinish() {
                viewBinding.timerText.text = "Start Movement"
                recording = videoCapture.output
                    .prepareRecording(this@MainActivity, mediaStoreOutputOptions)
                    .start(ContextCompat.getMainExecutor(this@MainActivity)) { recordEvent ->
                        when (recordEvent) {
                            is VideoRecordEvent.Start -> {
                                viewBinding.videoCaptureButton.apply {
                                    text = "Recording"
                                    setBackgroundColor(Color.RED)
                                    startTimer()
                                }
                            }
                            is VideoRecordEvent.Finalize -> {
                                if (!recordEvent.hasError()) {
                                    val videoUri = recordEvent.outputResults.outputUri
                                    val msg = "Video capture succeeded: " +
                                            "$videoUri"
                                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                                    Log.d(TAG, msg)
                                    viewBinding.videoCaptureButton.apply {
                                        val buttonColor = getColor(R.color.blue_primary)
                                        text = "Start"
                                        setBackgroundColor(buttonColor)
                                    }
                                    viewBinding.timerText.visibility = View.GONE

                                    val intent = Intent(this@MainActivity, ResultTranslateActivity::class.java)
                                    intent.putExtra("videoUri", videoUri.toString()) // Pass the videoUri as a string
                                    startActivity(intent)
                                    //predict(videoUri)

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

    companion object {
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
