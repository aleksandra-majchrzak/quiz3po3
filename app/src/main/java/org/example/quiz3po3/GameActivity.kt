package org.example.quiz3po3

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.SoundPool
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View.*
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.question_dialog_view.view.*
import org.example.quiz3po3.db.DbProvider
import org.example.quiz3po3.db.Question
import java.util.*


class GameActivity : AppCompatActivity(), MyRotationListener.RotationCallback {

    val questions = listOf(Question(1, "LAMA W KOSMOSIE"), Question(2, "GŁODNA GODZILLA"), Question(3, "WŚCIEKŁA WIEWIÓRKA"), Question(4, "SPIDER-PIG"))
    private val usedQuestionsIds = arrayListOf<Int>()
    private var successNumber = 0
    private var failNumber = 0

    private lateinit var sensorManager: SensorManager
    private lateinit var rotationSensor: Sensor

    private lateinit var soundPool: SoundPool
    private lateinit var rotationListener: MyRotationListener

    private var soundId = 0
    private var loaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        initSoundPool()
        initSensors()

        val question = questions[Random().nextInt(questions.size)]

        /* Alternative - get question from database, not from predefined list. */
        //val question = drawQuestion()
        Log.e("GameActivity", question?.toString() ?: "empty list")
        text.text = question?.text ?: ""

        /* Handle click on the screen, when question is answered incorrectly. */
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

    /** Initialize sounds played when question answered correctly. **/
    fun initSoundPool() {
        soundPool = SoundPool.Builder().setMaxStreams(1).build()
        soundPool.setOnLoadCompleteListener({ _, _, _ -> loaded = true })
        soundId = soundPool.load(this, R.raw.ok_sound, 1)
    }

    /** Initialize rotation sensor used to detect device rotation. **/
    fun initSensors() {
        sensorManager = getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) ?: sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        rotationListener = MyRotationListener(this, rotationSensor)
    }

   /** Get next random question from database. **/
    fun drawQuestion(): Question? {
        val question = DbProvider.getDatabase(applicationContext).questionDao().selectRandom(usedQuestionsIds)
        question?.let { usedQuestionsIds.add(question.id) }
        return question
    }

    /** Display next question. **/
    fun handleNextQuestion() {
        val nextQuestion = drawQuestion()
        nextQuestion?.let {
            text.text = it.text
        } ?: run {
            val textLayout = LayoutInflater.from(this).inflate(R.layout.question_dialog_view, null, false)
            textLayout.textView.text = "The end.\nSuccess: $successNumber\n Fail: $failNumber"
            textLayout.textView.gravity = Gravity.CENTER_HORIZONTAL
            AlertDialog.Builder(GameActivity@ this)
                    //.setMessage("The end.\nSuccess: $successNumber\n Fail: $failNumber")
                    .setView(textLayout)
                    .setPositiveButton("OK") { dialog, which -> finish() }
                    .show()
        }
    }

    /* Handle device rotation - question was answered correctly. */
    override fun onRotated() {
        successNumber++
        handleNextQuestion()
        generateSound2()
    }

    private fun generateSound2() {
        if (loaded)
            soundPool.play(soundId, 0.5f, 0.5f, 1, 0, 1f)
    }

}

class MyRotationListener(val callback: RotationCallback, val sensor: Sensor) : SensorEventListener {
    var isChecked = false

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    /* Called when there is a new sensor event. */
    override fun onSensorChanged(event: SensorEvent?) {
        if (sensor == event?.sensor) {
            //Log.d("new rotation", event.values.map { it.toString() }.toString())
            val rotation = event.values[1]
            if (!isChecked && Math.abs(rotation) > 0.85) {
                isChecked = true
                callback.onRotated()
            } else if (Math.abs(rotation) <= 0.85) {
                isChecked = false
            }
        }
    }

    /*fun generateSound() {
        val tg = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
        tg.startTone(ToneGenerator.TONE_PROP_PROMPT)
        Thread(Runnable {
            Thread.sleep(150)
            tg.stopTone()
        }).start()
    }*/

    interface RotationCallback {
        fun onRotated();
    }
}