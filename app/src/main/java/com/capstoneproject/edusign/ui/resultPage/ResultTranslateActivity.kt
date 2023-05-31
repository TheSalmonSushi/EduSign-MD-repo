package com.capstoneproject.edusign.ui.resultPage

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.data.model.Prediction
import com.capstoneproject.edusign.databinding.ActivityResultTranslateBinding
import com.capstoneproject.edusign.util.ViewModelFactory
import org.json.JSONArray


class ResultTranslateActivity : AppCompatActivity() {

    private lateinit var resultTranslateBinding: ActivityResultTranslateBinding
    private lateinit var viewModel: ResultTranslateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultTranslateBinding = ActivityResultTranslateBinding.inflate(layoutInflater)
        setContentView(resultTranslateBinding.root)

        val videoUriString = intent.getStringExtra("videoUri")
        val videoUri = Uri.parse(videoUriString)

        val viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory)[ResultTranslateViewModel::class.java]

        viewModel.performPrediction(videoUri)

        // Observe the LiveData for prediction result
        viewModel.predictionLiveData.observe(this) { prediction ->
            // Handle the prediction result
            handlePredictionResult(prediction)
        }

        // Observe the LiveData for error message
        viewModel.errorLiveData.observe(this) { errorMessage ->
            // Handle the error message
            handleErrorMessage(errorMessage)
        }

        // Observe the LiveData for loading state
        viewModel.loadingLiveData.observe(this) { isLoading ->
            if (isLoading) {
                // Show progress bar or disable buttons
                resultTranslateBinding.progressBar.visibility = View.VISIBLE
                resultTranslateBinding.translateResult.visibility = View.INVISIBLE
            } else {
                // Hide progress bar or enable buttons
                resultTranslateBinding.progressBar.visibility = View.GONE
                resultTranslateBinding.translateResult.visibility = View.VISIBLE
            }
        }
    }


    private fun handlePredictionResult(prediction: Prediction) {
        val predictionsList = prediction.prediction
        val valuesList = prediction.value

        val numberedPredictions = predictionsList.mapIndexed { index, value ->
            val predictionValue = valuesList.getOrNull(index) ?: ""
            "$value = $predictionValue"
        }
        val predictionsString = numberedPredictions.joinToString("\n")

        resultTranslateBinding.translateResult.text = predictionsString
    }

    private fun handleErrorMessage(error: String) {
        val errorMessage = "Prediction failed: $error"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}