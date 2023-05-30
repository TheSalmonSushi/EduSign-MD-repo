package com.capstoneproject.edusign.ui.resultPage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.edusign.data.model.Prediction
import com.capstoneproject.edusign.data.repository.remote.ApiConfig
import com.capstoneproject.edusign.data.repository.remote.ApiService
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class ResultTranslateViewModel : ViewModel() {

    val predictionLiveData: MutableLiveData<Prediction> = MutableLiveData()
    val errorLiveData: MutableLiveData<String> = MutableLiveData()

    fun uploadVideo(videoPart: MultipartBody.Part) {
        val client = ApiConfig.getApiService().uploadVideo(videoPart)
        client.enqueue(object : retrofit2.Callback<Prediction> {
            override fun onResponse(call: Call<Prediction>, response: Response<Prediction>) {
                if (response.isSuccessful) {
                    val predictionResponse = response.body()
                    predictionLiveData.postValue(predictionResponse)
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorLiveData.postValue(errorBody ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<Prediction>, t: Throwable) {
                errorLiveData.postValue(t.message ?: "Unknown error")
            }
        })
    }
}


