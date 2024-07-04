package com.farhanadi.moodbotapp.view.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.view.other.Message
import com.farhanadi.moodbotapp.view.other.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    private lateinit var chatRecyclerView: RecyclerView;
    private lateinit var userInput:EditText;
    private lateinit var sendBtn:ImageView;
    private val messageList: MutableList<Message> = mutableListOf();
    private val messageAdapter: MessageAdapter by lazy { MessageAdapter(messageList) }
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase timeout to 30 seconds
        .readTimeout(30, TimeUnit.SECONDS)   // Increase read timeout to 30 seconds
        .build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_bot)
        chatRecyclerView= findViewById(R.id.rv_chatbot);
        userInput= findViewById(R.id.userInput);
        sendBtn= findViewById(R.id.user_send_btn);
        val sapa1= "Hai! Saya Moodie, chatbot yang siap menemani dan membantu kamu hari ini.";
        val sapa2= "Jangan ragu untuk memulai percakapan, dan aku akan berusaha semaksimal mungkin untuk menjawab.";
        val sapa3= "Oh ya, jika kamu ingin mengakhiri percakapan, cukup katakan \"selamat tinggal\" atau \"goodbye\". Terima kasih sudah berinteraksi dengan Moodie!"
        messageList.add(Message(sapa1,MessageType.SENT_BY_BOT));
        messageList.add(Message(sapa2,MessageType.SENT_BY_BOT));
        messageList.add(Message(sapa3,MessageType.SENT_BY_BOT));
        // Setup recycler view
        chatRecyclerView.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        chatRecyclerView.layoutManager = layoutManager

        sendBtn.setOnClickListener(View.OnClickListener {
            val userQuestion = userInput.text.toString().trim();
            addToChat(userQuestion,MessageType.SENT_BY_USER);
            userInput.text.clear()
            callAPI(userQuestion)
        })
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
    }

    private fun callAPI(question: String) {
        messageList.add(Message("Typing...", MessageType.SENT_BY_BOT))

        val jsonBody = JSONObject().apply {
            put("text", question)
        }

        val jsonString = jsonBody.toString();
        val mediaType = "application/json; charset=utf-8".toMediaType();
        val requestBody = jsonString.toRequestBody(mediaType);
//        Toast.makeText(this, jsonString, Toast.LENGTH_SHORT).show();
        Log.d("JSON Request", jsonString)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("https://moodbotapp-2bfesfcx6q-as.a.run.app/api/chatbot") // Replace with your actual API URL
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseString = response.body?.string() ?: ""

                withContext(Dispatchers.Main) {
                    // Process response on the main thread
                    if (responseString.isNotEmpty()) {
                        val jsonObject = JSONObject(responseString)
                        val responseText = jsonObject.getString("response")
                        addResponse(responseText.trim())
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
}