package com.capstoneproject.edusign.data.model

import com.google.gson.annotations.SerializedName

data class Prediction(
    val prediction: List<String>,
    val value: List<String>
)
