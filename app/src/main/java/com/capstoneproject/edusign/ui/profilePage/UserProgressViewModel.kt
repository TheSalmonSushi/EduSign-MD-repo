package com.capstoneproject.edusign.ui.profilePage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstoneproject.edusign.data.db.ChallengeRepository
import kotlinx.coroutines.launch

class UserProgressViewModel(private val challengeRepos: ChallengeRepository): ViewModel() {

    val completionPercentageLiveData: LiveData<Float> = MutableLiveData()

    init {
        viewModelScope.launch {
            val completionPercentage = calculateCompletionPercentage()
            (completionPercentageLiveData as MutableLiveData).value = completionPercentage
        }
    }

    suspend fun calculateCompletionPercentage(): Float {
        val answeredQuestions = challengeRepos.getAnsweredQuestions()
        val totalQuestions = 18  // Total number of questions

        val completionPercentage = (answeredQuestions.size.toFloat() / totalQuestions) * 100
        return completionPercentage
    }
}