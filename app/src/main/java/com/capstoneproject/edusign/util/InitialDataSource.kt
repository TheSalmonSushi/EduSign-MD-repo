package com.capstoneproject.edusign.util

import com.capstoneproject.edusign.data.db.ChallengeEntity
import com.capstoneproject.edusign.data.db.QuestionEntity

object InitialDataSource {

    fun getChallenges(): List<ChallengeEntity>{
        return listOf(
            ChallengeEntity(1, "Hewan"),
            ChallengeEntity(2, "Anggota Tubuh"),
            ChallengeEntity(3, "Keluarga"),
            ChallengeEntity(4, "Warna"),
            ChallengeEntity(5, "Pekerjaan"),
            ChallengeEntity(6, "Sapaan")
        )
    }

    fun getQuestions(): List<QuestionEntity>{
        return listOf(
            QuestionEntity(1, 1, "Gajah", false),
            QuestionEntity(2, 1, "Serigala", false),
            QuestionEntity(3, 1, "Lebah", false),
            QuestionEntity(4, 2, "Telinga", false),
            QuestionEntity(5, 2, "Mata", false),
            QuestionEntity(6, 2, "Hidung", false),
            QuestionEntity(7, 3, "Ayah", false),
            QuestionEntity(8, 3, "Ibu", false),
            QuestionEntity(9, 3, "Nenek", false),
            QuestionEntity(10, 4, "Biru", false),
            QuestionEntity(11, 4, "Putih", false),
            QuestionEntity(12, 4, "Hitam", false),
            QuestionEntity(13, 5, "Polisi", false),
            QuestionEntity(14, 5, "Pemadam Kebakaran", false),
            QuestionEntity(15, 5, "Membaca", false),
            QuestionEntity(16, 6, "Halo", false),
            QuestionEntity(17, 6, "Terimakasih", false),
            QuestionEntity(18, 6, "Tidak", false)
        )
    }
}