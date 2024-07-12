package com.farhanadi.moodbotapp.view.camera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.view.chat.ChatBotActivity
import com.farhanadi.moodbotapp.view.main.MainActivity

class CameraConfirmActivity : AppCompatActivity() {

    private lateinit var emotion: String
    private var selectedTextView: TextView? = null

    private lateinit var confirmHappy: TextView
    private lateinit var confirmAngry: TextView
    private lateinit var confirmSad: TextView
    private lateinit var emotionEmoticon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_confirm)

        // Get the emotion from the previous activity
        emotion = intent.getStringExtra("emotion") ?: ""

        // Initialize views
        confirmHappy = findViewById(R.id.confirm_happy)
        confirmAngry = findViewById(R.id.confirm_angry)
        confirmSad = findViewById(R.id.confirm_sad)
        emotionEmoticon = findViewById(R.id.emotion_emoticon)

        // Set initial selection based on the emotion
        when (emotion) {
            "happy" -> selectEmotion(confirmHappy, R.drawable.emo_happy)
            "angry" -> selectEmotion(confirmAngry, R.drawable.emo_angry)
            "sad" -> selectEmotion(confirmSad, R.drawable.emo_sad)
        }

        findViewById<ImageView>(R.id.btn_back).setOnClickListener(){
            val intent = Intent(this@CameraConfirmActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // Set click listeners for emotion TextViews
        confirmHappy.setOnClickListener {
            selectEmotion(confirmHappy, R.drawable.emo_happy)
        }
        confirmAngry.setOnClickListener {
            selectEmotion(confirmAngry, R.drawable.emo_angry)
        }
        confirmSad.setOnClickListener {
            selectEmotion(confirmSad, R.drawable.emo_sad)
        }

        // Set click listener for confirm button
        findViewById<ImageView>(R.id.confirm_button).setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun selectEmotion(textView: TextView, drawableRes: Int) {
        // Reset the previously selected TextView
        selectedTextView?.apply {
            setBackgroundResource(0)
            setTextColor(ContextCompat.getColor(this@CameraConfirmActivity, R.color.black))
        }

        // Set the new selected TextView
        selectedTextView = textView.apply {
            setBackgroundResource(R.drawable.bg_rounded_7)
            setTextColor(ContextCompat.getColor(this@CameraConfirmActivity, R.color.white))
        }

        // Update the emotion emoticon
        emotionEmoticon.setImageResource(drawableRes)

        // Update the emotion value
        emotion = when (textView.id) {
            R.id.confirm_happy -> "senang"
            R.id.confirm_angry -> "marah"
            R.id.confirm_sad -> "sedih"
            else -> ""
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setMessage("Emosi yang kamu pilih sudah sesuai?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, ChatBotActivity::class.java).apply {
                    putExtra("emotion", emotion)
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
