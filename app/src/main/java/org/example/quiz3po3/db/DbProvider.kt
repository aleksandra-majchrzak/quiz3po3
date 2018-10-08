package org.example.quiz3po3.db

import android.content.Context
import com.huma.room_for_asset.RoomAsset

class DbProvider {
    companion object {
        private var db: QuestionDb? = null

        fun getDatabase(context: Context): QuestionDb {
            if (db == null) {
                db = RoomAsset.databaseBuilder(context.applicationContext, QuestionDb::class.java, "quiz_3po3_db.db")
                        .allowMainThreadQueries()   //todo shouldn't be done in production
                        .build()
            }
            return db!!
        }
    }
}