package com.capstoneproject.edusign.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChallengePicture(
    val name: String,
    val photo: Int,
) : Parcelable
