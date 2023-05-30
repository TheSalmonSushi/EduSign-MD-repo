package com.capstoneproject.edusign.data.repository.remote

import com.capstoneproject.edusign.data.model.Prediction
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("/predict")
    fun uploadVideo(
        @Part file: MultipartBody.Part,
    ): Call<Prediction>

}