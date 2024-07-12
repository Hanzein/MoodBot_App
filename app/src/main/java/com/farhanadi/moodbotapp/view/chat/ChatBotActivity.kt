package com.farhanadi.moodbotapp.view.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.view.main.MainActivity
import com.farhanadi.moodbotapp.view.other.Message
import com.farhanadi.moodbotapp.view.other.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.RequestBody.Companion;
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response;
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatBotActivity : AppCompatActivity() {
    private lateinit var emotion: String
    private lateinit var chatRecyclerView: RecyclerView;
    private lateinit var userInput:EditText;
    private lateinit var sendBtn:ImageView;
    private lateinit var endBtn:TextView;
    private lateinit var bottomLayout: RelativeLayout;
    private val messageList: MutableList<Message> = mutableListOf();
    private val messageAdapter: MessageAdapter by lazy { MessageAdapter(messageList) }
    private val client = OkHttpClient.Builder()
        .connectTimeout(50, TimeUnit.SECONDS) // Increase timeout to 30 seconds
        .readTimeout(50, TimeUnit.SECONDS)   // Increase read timeout to 30 seconds
        .build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)
        chatRecyclerView= findViewById(R.id.rv_chatbot);
        userInput= findViewById(R.id.userInput);
        sendBtn= findViewById(R.id.user_send_btn);
        endBtn=findViewById(R.id.btn_end);
        bottomLayout=findViewById(R.id.bottom_layout);

//      Initialize chat from bot for first load
        CoroutineScope(Dispatchers.Main).launch {
            addInitialBotMessages()
            emotion = intent.getStringExtra("emotion") ?: ""
            delay(1000)
            if (emotion.isNotEmpty()){
                callAPI(emotion);
            }
        }
        // Setup recycler view
        chatRecyclerView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = layoutManager

        sendBtn.setOnClickListener(View.OnClickListener {
            val userQuestion = userInput.text.toString().trim();
            if (userQuestion.isNotEmpty()){
                addToChat(userQuestion,MessageType.SENT_BY_USER);
                userInput.text.clear()
                userInput.isEnabled=false
                callAPI(userQuestion)
            }else{
                Toast.makeText(this, "Tuliskan pertanyaanmu", Toast.LENGTH_SHORT).show();

            }
        })

        endBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@ChatBotActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }

    private suspend fun addInitialBotMessages() {
        val messages = listOf(
            "Hai! Saya Moodie, chatbot yang siap menemani dan membantu kamu hari ini.",
            "Jangan ragu untuk memulai percakapan, dan aku akan berusaha semaksimal mungkin untuk menjawab.",
            "Oh ya, jika kamu ingin mengakhiri percakapan, cukup katakan \"selamat tinggal\" atau \"goodbye\". Terima kasih sudah berinteraksi dengan Moodie!"
        )

        for (message in messages) {
            delay(1000) // Delay 1 second between each message
            addToChat(message, MessageType.SENT_BY_BOT)
        }
    }
    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }
    private fun addResponse(response: String) {
        messageList.removeAt(messageList.lastIndex)
        addToChat(response, MessageType.SENT_BY_BOT)
        userInput.isEnabled=true
    }

    private fun callAPI(question: String) {
        messageList.add(Message("Typing...", MessageType.SENT_BY_BOT))

        val jsonBody = JSONObject().apply {
            put("text", question)
        }

        val jsonString = jsonBody.toString();
        val mediaType = "application/json; charset=utf-8".toMediaType();
        val requestBody = jsonString.toRequestBody(mediaType);
        Log.d("JSON Request", jsonString)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("https://moodbotapps-2bfesfcx6q-as.a.run.app/api/chatbot") // Replace with your actual API URL
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                withContext(Dispatchers.Main) {
                    // Process response on the main thread
                    if (responseString.isNotEmpty()) {
                        val jsonObject = JSONObject(responseString)
                        val responseText = jsonObject.getString("response")
                        val responseTag= jsonObject.getString("tag")
                        if (responseTag.equals("bye")){
                            showConfirmationDialog(responseText.trim())
                        }else{
                            addResponse(responseText.trim())
                        }
                    } else {
                        addResponse("Failed to load response")
                    }
                }
            } catch (e: Exception) {
                Log.e("API Error", "Request failed", e)
                e.printStackTrace()
                addResponse("Failed to load response due tos ${e.message}")
            }
        }
    }

    private fun showConfirmationDialog(responseText: String) {
        AlertDialog.Builder(this)
            .setMessage("Mengakhiri pembicaraan?")
            .setPositiveButton("Yes") { _, _ ->
                addResponse(responseText.trim())
                endBtn.visibility = View.VISIBLE
                bottomLayout.visibility = View.GONE
            }
            .setNegativeButton("No") { _, _ ->
                messageList.removeAt(messageList.size - 1) // Remove "bye" message
                messageList.removeAt(messageList.size - 1)
                messageAdapter.notifyDataSetChanged()
                chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount)
            }
            .show()
    }
}