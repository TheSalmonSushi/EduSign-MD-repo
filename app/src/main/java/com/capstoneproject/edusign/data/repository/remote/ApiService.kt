package com.capstoneproject.edusign.data.repository.remote

import com.capstoneproject.edusign.data.model.Prediction
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("/predict")
    suspend fun uploadVideo(
        @Part file: MultipartBody.Part,
    ): Prediction

}