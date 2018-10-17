package org.example.quiz3po3

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton.setOnClickListener { _ ->
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, GameActivity::class.java))
        }
    }
}
