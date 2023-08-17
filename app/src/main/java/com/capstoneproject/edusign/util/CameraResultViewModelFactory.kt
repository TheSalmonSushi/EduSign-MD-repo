package com.capstoneproject.edusign.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.ui.resultPage.ResultTranslateViewModel

class CameraResultViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultTranslateViewModel::class.java)) {
            return ResultTranslateViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}