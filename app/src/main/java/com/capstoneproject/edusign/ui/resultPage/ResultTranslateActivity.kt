package com.capstoneproject.edusign.ui.resultPage

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.data.model.Prediction
import com.capstoneproject.edusign.databinding.ActivityResultTranslateBinding
import com.capstoneproject.edusign.ui.camera.MainActivity
import com.capstoneproject.edusign.ui.homeActivity.HomeActivity
import com.capstoneproject.edusign.util.CameraResultViewModelFactory

class ResultTranslateActivity : AppCompatActivity() {

    private lateinit var resultTranslateBinding: ActivityResultTranslateBinding
    private lateinit var viewModel: ResultTranslateViewModel
    private lateinit var videoView: VideoView
    private lateinit var videoPlaybackServiceIntent: Intent
    private var currentPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultTranslateBinding = ActivityResultTranslateBinding.inflate(layoutInflater)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(resultTranslateBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        videoView = resultTranslateBinding.previewVideo

        val videoUriString = intent.getStringExtra("videoUri")
        val videoUri = Uri.parse(videoUriString)

        val cameraResultViewModelFactory = CameraResultViewModelFactory(application)
        viewModel = ViewModelProvider(this, cameraResultViewModelFactory)[ResultTranslateViewModel::class.java]

        viewModel.performPrediction(videoUri)

        viewModel.predictionLiveData.observe(this) { prediction ->
            handlePredictionResult(prediction)
        }

        viewModel.errorLiveData.observe(this) { errorMessage ->
            handleErrorMessage(errorMessage)
        }

        viewModel.loadingLiveData.observe(this) { isLoading ->
            if (isLoading) {
                resultTranslateBinding.progressBar.visibility = View.VISIBLE
                resultTranslateBinding.translateResult.visibility = View.INVISIBLE
            } else {
                resultTranslateBinding.progressBar.visibility = View.GONE
                resultTranslateBinding.translateResult.visibility = View.VISIBLE
            }
        }

        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setOnVideoSizeChangedListener { _, _, _ ->
                // Calculate the aspect ratio of the video
                val videoWidth = mediaPlayer.videoWidth
                val videoHeight = mediaPlayer.videoHeight
                val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()

                // Calculate the dimensions of the VideoView
                val screenWidth = videoView.width
                val screenHeight = videoView.height
                val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()

                // Adjust the dimensions of the VideoView to maintain aspect ratio
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

        resultTranslateBinding.restartVideo.setOnClickListener {
            val videoUriString = intent.getStringExtra("videoUri")
            val videoUri = Uri.parse(videoUriString)

            val contentResolver: ContentResolver = contentResolver
            contentResolver.delete(videoUri, null, null)


            val intent = Intent(this@ResultTranslateActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("restartMainActivity", true)
            startActivity(intent)
        }

        // Start the VideoPlaybackService
        videoPlaybackServiceIntent = Intent(this, ResultTranslateVideoService::class.java)
        videoPlaybackServiceIntent.putExtra("videoUri", videoUriString)
        startService(videoPlaybackServiceIntent)
    }

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
        val videoUriString = intent.getStringExtra("videoUri")
        val videoUri = Uri.parse(videoUriString)

        val contentResolver: ContentResolver = contentResolver
        contentResolver.delete(videoUri, null, null)

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("selectedFragment", R.id.navigation_challenge)
        startActivity(intent)
    }

    private fun handlePredictionResult(prediction: Prediction) {
        val predictionsList = prediction.prediction
        val valuesList = prediction.value

        // nge test yang kata pertama aja
        val firstPrediction = predictionsList.firstOrNull() ?: ""
        val firstValue = valuesList.firstOrNull() ?: ""

        val formattedResult = "$firstPrediction"
        resultTranslateBinding.translateResult.text = formattedResult

        // ini yang pake list
        // val numberedPredictions = predictionsList.mapIndexed { index, value ->
        //     val predictionValue = valuesList.getOrNull(index) ?: ""
        //     "$value = $predictionValue %"
        // }
        // val predictionsString = numberedPredictions.joinToString("\n")
        // resultTranslateBinding.translateResult.text = predictionsString
    }

    private fun handleErrorMessage(error: String) {
        val errorMessage = "Prediction failed: $error"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}