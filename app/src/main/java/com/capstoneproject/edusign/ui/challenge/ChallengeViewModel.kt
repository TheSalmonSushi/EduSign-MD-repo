package com.capstoneproject.edusign.ui.challenge

import androidx.lifecycle.ViewModel
import com.capstoneproject.edusign.data.db.ChallengeRepository

class ChallengeViewModel(private val challengeRepos: ChallengeRepository): ViewModel() {

    val challengeList = challengeRepos.getChallenges()

}