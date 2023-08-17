package com.capstoneproject.edusign.data.db

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: Int,
    val name: String
) : Parcelable

@Parcelize
@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val challengeId: Int,
    val kata: String,
    var isAnswered: Boolean,
) : Parcelable


data class ChallengeWithQuestions(
    @Embedded val challenge: ChallengeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "challengeId",
    )
    val questions: List<QuestionEntity>
)

