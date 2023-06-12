package com.capstoneproject.edusign.data.repository.remote2

import com.capstoneproject.edusign.data.model.QuizResponse
import com.capstoneproject.edusign.data.model.QuizTypeResponse
import com.capstoneproject.edusign.data.model.UserDictionaryResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService2 {

    @GET("/get/dict")
    fun getAllWords(
    ): Call<List<UserDictionaryResponse>>

    @FormUrlEncoded
    @POST("/get/quiz")
    fun quizQuestion(
        @Field("namaquiz") namaquiz: String,
    ): Call<List<QuizResponse>>

    @GET("/get/type")
    fun getQuizType(
    ): Call<List<QuizTypeResponse>>










}