package com.capstoneproject.edusign.ui.resultPage

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.R
import com.capstoneproject.edusign.data.model.Prediction
import com.capstoneproject.edusign.databinding.ActivityResultTranslateBinding
import com.capstoneproject.edusign.ui.camera.MainActivity
import com.capstoneproject.edusign.ui.homeActivity.HomeActivity
import com.capstoneproject.edusign.util.ViewModelFactory

class ResultTranslateActivity : AppCompatActivity() {

    private lateinit var resultTranslateBinding: ActivityResultTranslateBinding
    private lateinit var viewModel: ResultTranslateViewModel
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultTranslateBinding = ActivityResultTranslateBinding.inflate(layoutInflater)
        setContentView(resultTranslateBinding.root)

        videoView = resultTranslateBinding.previewVideo

        val videoUriString = intent.getStringExtra("videoUri")
        val videoUri = Uri.parse(videoUriString)

        val viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[ResultTranslateViewModel::class.java]

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

        // for video preview
        videoView.setVideoURI(videoUri)
        videoView.start()
        // Loop the video playback
        videoView.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }

        resultTranslateBinding.restartVideo.setOnClickListener {
            val intent = Intent(this@ResultTranslateActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra("restartMainActivity", true)
            startActivity(intent)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the resources used by the VideoView
        videoView.stopPlayback()
    }



    override fun onBackPressed() {
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
//        val numberedPredictions = predictionsList.mapIndexed { index, value ->
//            val predictionValue = valuesList.getOrNull(index) ?: ""
//            "$value = $predictionValue %"
//        }
//        val predictionsString = numberedPredictions.joinToString("\n")
//
//        resultTranslateBinding.translateResult.text = predictionsString
    }

    private fun handleErrorMessage(error: String) {
        val errorMessage = "Prediction failed: $error"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}