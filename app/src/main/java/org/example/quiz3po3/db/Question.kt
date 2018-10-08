package org.example.quiz3po3.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(@ColumnInfo(name = "id") @PrimaryKey val id: Int, @ColumnInfo(name = "text") val text: String) {
}