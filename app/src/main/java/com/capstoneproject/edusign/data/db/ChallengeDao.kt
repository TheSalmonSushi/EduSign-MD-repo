package com.capstoneproject.edusign.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ChallengeDao {

    @Transaction
    @Query("SELECT * FROM challenges")
    fun getAllChallengesWithQuestions(): List<ChallengeWithQuestions>

    @Query("SELECT * FROM challenges WHERE id = :challengesId")
    fun getChallengesWithQuestionById(challengesId: Int): LiveData<ChallengeWithQuestions>

    @Query("SELECT * FROM challenges")
    fun getChallenges(): LiveData<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :challengesId")
    fun getChallengesById(challengesId: Int): LiveData<ChallengeEntity>

    @Update
    fun updateQuestion(questionEntity: QuestionEntity)

    @Update
    fun updateQuestions(questionEntity: List<QuestionEntity>): Int

    @Insert
    fun insertAllChallenges(challengeEntity: List<ChallengeEntity>)

    @Insert
    fun insertAllQuestions(questionEntity: List<QuestionEntity>)

    @Query("SELECT * FROM questions WHERE isAnswered = 1")
    fun getAnsweredQuestions(): List<QuestionEntity>




}