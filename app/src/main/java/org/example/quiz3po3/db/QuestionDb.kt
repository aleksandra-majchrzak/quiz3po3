package org.example.quiz3po3.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Question::class], version = 2, exportSchema = false)
abstract class QuestionDb : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}