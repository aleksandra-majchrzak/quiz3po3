package org.example.quiz3po3

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View.*
import kotlinx.android.synthetic.main.activity_game.*
import org.example.quiz3po3.db.DbProvider
import org.example.quiz3po3.db.Question

class GameActivity : AppCompatActivity() {

    private val usedQuestionsIds = arrayListOf<Int>()
    private var successNumber = 0
    private var failNumber = 0

    private lateinit var sensorManager: SensorManager
    private lateinit var rotationSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        sensorManager = getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) ?: sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)

        /*val questions = DbProvider.getDatabase(applicationContext).questionDao().selectAll()
        Log.e("GameActivity", questions.toString())*/

        //val questions = listOf(Question(1, "LAMA W KOSMOSIE"), Question(2, "GŁODNA GODZILLA"), Question(3, "WŚCIEKŁA WIEWIÓRKA"), Question(4, "SPIDER-PIG"))
        //val question = questions.get(Math.abs(Random().nextInt()) % 4)

        val question = drawQuestion()
        Log.e("GameActivity", question?.toString() ?: "empty list")
        text.text = question?.text ?: ""

        questionCardView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                failNumber++
                handleNextQuestion()
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_IMMERSIVE_STICKY or SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION
        sensorManager.registerListener(rotationListener, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(rotationListener)
    }

    fun drawQuestion(): Question? {
        val question = DbProvider.getDatabase(applicationContext).questionDao().selectRandom(usedQuestionsIds)
        question?.let { usedQuestionsIds.add(question.id) }
        return question
    }

    fun handleNextQuestion() {
        val nextQuestion = drawQuestion()
        nextQuestion?.let {
            text.text = it.text
        } ?: run {
            AlertDialog.Builder(GameActivity@ this)
                    .setMessage("The end.\nSuccess: $successNumber\n Fail: $failNumber")
                    .setNeutralButton("OK") { dialog, which -> finish() }
                    .show()
        }
    }

    private val rotationListener = object : SensorEventListener {
        var isChecked = false

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (rotationSensor == event?.sensor) {
                //Log.d("new rotation", event.values.map { it.toString() }.toString())
                val rotation = event.values[1]
                if (!isChecked && Math.abs(rotation) > 0.85) {
                    isChecked = true
                    successNumber++
                    handleNextQuestion()
                } else if (Math.abs(rotation) <= 0.85) {
                    isChecked = false
                }
            }
        }
    }
}
