package com.capstoneproject.edusign.ui.dictionary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstoneproject.edusign.data.model.UserDictionaryResponse
import com.capstoneproject.edusign.data.repository.remote2.ApiConfig2
import retrofit2.Call
import retrofit2.Response

class DictionaryViewModel : ViewModel() {

    val wordsLiveData: MutableLiveData<List<UserDictionaryResponse>> = MutableLiveData()
    val errorLiveData: MutableLiveData<String> = MutableLiveData()
    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun fetchWords() {
        loadingLiveData.value = true

        val apiService2 = ApiConfig2.getApiService2()
        val call = apiService2.getAllWords()

        call.enqueue(object : retrofit2.Callback<List<UserDictionaryResponse>> {
            override fun onResponse(
                call: Call<List<UserDictionaryResponse>>,
                response: Response<List<UserDictionaryResponse>>
            ) {
                loadingLiveData.value = false

                if (response.isSuccessful) {
                    val words = response.body()
                    wordsLiveData.value = words!!
                } else {
                    val errorMessage = "Error: ${response.message()}"
                    errorLiveData.value = errorMessage
                }
            }

            override fun onFailure(call: Call<List<UserDictionaryResponse>>, t: Throwable) {
                loadingLiveData.value = false

                val errorMessage = "Failure: ${t.message}"
                errorLiveData.value = errorMessage
            }
        })
    }
}