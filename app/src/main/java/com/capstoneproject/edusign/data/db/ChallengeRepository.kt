package com.capstoneproject.edusign.data.db

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val executor: ExecutorService
) {

    companion object {

        @Volatile
        private var instance: ChallengeRepository? = null

        fun getInstance(context: Context, applicationScope: CoroutineScope): ChallengeRepository {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    val database = ChallengeDatabase.getInstance(context, applicationScope)
                    instance = ChallengeRepository(
                        database.challengeDao(),
                        Executors.newSingleThreadExecutor()
                    )
                }
                return instance as ChallengeRepository
            }

        }
    }

    // Function to get all challenges with questions
    suspend fun getAllChallengesWithQuestions(): List<ChallengeWithQuestions> {
        return challengeDao.getAllChallengesWithQuestions()
    }

    // Function to get challenges as LiveData
    fun getChallenges(): LiveData<List<ChallengeEntity>> {
        return challengeDao.getChallenges()
    }

    // Function to get a challenge by its ID as LiveData
    fun getChallengeById(challengesId: Int): LiveData<ChallengeEntity> {
        return challengeDao.getChallengesById(challengesId)
    }

    // Function to update a question entity
    suspend fun updateQuestion(questionEntity: QuestionEntity) {
        executor.execute { challengeDao.updateQuestion(questionEntity) }
    }

    // Function to update a list of question entities
    fun updateQuestions(questionEntities: List<QuestionEntity>) {
        executor.execute { challengeDao.updateQuestions(questionEntities) }
    }

    fun getChallengesWithQuestionById(challengesId: Int): LiveData<ChallengeWithQuestions> {
        return challengeDao.getChallengesWithQuestionById(challengesId)
    }

    suspend fun getAnsweredQuestions(): List<QuestionEntity> {
        return withContext(Dispatchers.IO) {
            challengeDao.getAnsweredQuestions()
        }
    }




}

