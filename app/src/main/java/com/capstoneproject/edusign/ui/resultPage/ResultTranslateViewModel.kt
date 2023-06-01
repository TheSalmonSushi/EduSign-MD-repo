package com.capstoneproject.edusign.ui.resultPage

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstoneproject.edusign.data.model.Prediction
import com.capstoneproject.edusign.data.repository.remote.ApiConfig
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ResultTranslateViewModel(application: Application) : ViewModel() {

    private val context: Context = application.applicationContext

    private val _predictionLiveData = MutableLiveData<Prediction>()
    val predictionLiveData: LiveData<Prediction> = _predictionLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    fun performPrediction(videoUri: Uri) {
        viewModelScope.launch {
            _loadingLiveData.value = true
            val apiService = ApiConfig.getApiService()
            val file = convertUriToFile(context, videoUri)
            val requestBody = file.asRequestBody("video/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("video", file.name, requestBody)
            try {
                val prediction = apiService.uploadVideo(part)
                handlePredictionResult(prediction)
            } catch (e: Exception) {
                handleError(e)
            }
            _loadingLiveData.value = false
        }
    }

    private fun convertUriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp", null, context.cacheDir)
        tempFile.deleteOnExit()

        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }

    private fun handlePredictionResult(prediction: Prediction) {
        _predictionLiveData.value = prediction
    }

    private fun handleError(error: Exception) {
        val errorMessage = "Prediction failed: ${error.message}"
        _errorLiveData.value = errorMessage
    }
}
