package com.capstoneproject.edusign.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.data.repository.remote.ApiService
import com.capstoneproject.edusign.ui.resultPage.ResultTranslateViewModel

class ViewModelFactory(

) :
    ViewModelProvider.NewInstanceFactory(
    ) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultTranslateViewModel::class.java)) {
            return ResultTranslateViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
