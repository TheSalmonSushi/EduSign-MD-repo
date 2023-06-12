package com.capstoneproject.edusign.data.model

import com.google.gson.annotations.SerializedName

data class QuizTypeResponse(
    @SerializedName("quiztypeid")
    val quizTypeId: Int,
    @SerializedName("namaquiz")
    val namaQuiz: String
)