package com.capstoneproject.edusign.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizResponse(
    @SerializedName("id_quiz")
    val idQuiz: Int,
    @SerializedName("soal")
    val soal: String,
    @SerializedName("link_video")
    val linkVideo: String,
    @SerializedName("opsi1")
    val opsi1: String,
    @SerializedName("opsi2")
    val opsi2: String,
    @SerializedName("opsi3")
    val opsi3: String,
    @SerializedName("jawaban")
    val jawaban: String
):Parcelable