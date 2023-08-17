package com.capstoneproject.edusign.ui.detailChallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.capstoneproject.edusign.data.db.ChallengeRepository
import com.capstoneproject.edusign.data.db.ChallengeWithQuestions
import com.capstoneproject.edusign.data.db.QuestionEntity
import kotlinx.coroutines.launch

class DetailChallengeViewModel (private val challengeRepos: ChallengeRepository, ): ViewModel() {

    val _challengesId = MutableLiveData<Int>()

    private val _challengeWithQuestions = _challengesId.switchMap { id->
        challengeRepos.getChallengesWithQuestionById(id)
    }
    val challengesWithQuestions: LiveData<ChallengeWithQuestions> = _challengeWithQuestions


    private var currentItemIndex: Int = 0
    fun setCurrentItemIndex(index: Int) {
        currentItemIndex = index
    }
    fun getCurrentItemIndex(): Int {
        return currentItemIndex
    }



    fun setChallengesId(challengesId: Int) {
        if (challengesId == _challengesId.value) {
            return
        }

        _challengesId.value = challengesId
    }


    fun updateQuestion(questionEntity: QuestionEntity) {
        viewModelScope.launch {
            challengeRepos.updateQuestion(questionEntity)
        }
    }




}