package com.capstoneproject.edusign.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDictionaryResponse(
    @field:SerializedName("id_word")
    val id_word: Int,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("link")
    val link: String? = null
) : Parcelable