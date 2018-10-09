package org.example.quiz3po3.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    fun selectAll(): List<Question>

    @Query("SELECT * FROM questions WHERE id NOT IN (:ids) ORDER BY RANDOM() LIMIT 1")
    fun selectRandom(ids: List<Int>): Question?
}