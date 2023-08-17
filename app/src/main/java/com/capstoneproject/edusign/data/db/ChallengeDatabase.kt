package com.capstoneproject.edusign.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.capstoneproject.edusign.util.InitialDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [ChallengeEntity::class, QuestionEntity::class], version = 1, exportSchema = false)
abstract class ChallengeDatabase: RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao

    companion object {
        private var instance: ChallengeDatabase? = null

        @Synchronized
        fun getInstance(context: Context,  applicationScope: CoroutineScope): ChallengeDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChallengeDatabase::class.java,
                    "challenge.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            instance?.let { database ->
                                applicationScope.launch {
                                    val challengeDao = database.challengeDao()
                                    challengeDao.insertAllChallenges(InitialDataSource.getChallenges())
                                    challengeDao.insertAllQuestions(InitialDataSource.getQuestions())
                                }
                            }
                        }
                    })
                    .build()
            }
            return instance!!
        }
    }

}