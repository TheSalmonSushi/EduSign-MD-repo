package com.capstoneproject.edusign.data.repository.remote.predictionService

import com.capstoneproject.edusign.data.model.Prediction
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

//interface PredictionService {
//
//    @Multipart
//    @POST("/predict")
//    suspend fun uploadVideo(
//        @Part video: MultipartBody.Part
//    ): Prediction
//}