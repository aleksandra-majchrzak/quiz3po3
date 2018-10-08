package org.example.quiz3po3

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.*
import kotlinx.android.synthetic.main.activity_game.*
import org.example.quiz3po3.db.DbProvider

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        /*val questions = DbProvider.getDatabase(applicationContext).questionDao().selectAll()
        Log.e("GameActivity", questions.toString())*/

        val question = DbProvider.getDatabase(applicationContext).questionDao().selectRandom(arrayOf())
        Log.e("GameActivity", question?.toString() ?: "empty list")
        text.text = question?.text ?: ""
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_IMMERSIVE_STICKY or SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}
