package com.capstoneproject.edusign.ui.challenge

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstoneproject.edusign.data.db.ChallengeRepository
import com.capstoneproject.edusign.ui.detailChallenge.DetailChallengeViewModel
import com.capstoneproject.edusign.ui.profilePage.UserProgressViewModel
import kotlinx.coroutines.CoroutineScope

class ChallengeViewModelFactory private constructor(private val repository: ChallengeRepository) :
    ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var instance: ChallengeViewModelFactory? = null

        fun getInstance(context: Context, applicationScope: CoroutineScope): ChallengeViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ChallengeViewModelFactory(
                    ChallengeRepository.getInstance(context, applicationScope)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ChallengeViewModel::class.java) -> {
                ChallengeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailChallengeViewModel::class.java) -> {
                DetailChallengeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(UserProgressViewModel::class.java) -> {
                UserProgressViewModel(repository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
}

