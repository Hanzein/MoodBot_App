package com.farhanadi.moodbotapp.view.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farhanadi.moodbotapp.R
import com.farhanadi.moodbotapp.databinding.FragmentChatBinding
import com.farhanadi.moodbotapp.view.camera.CameraActivity
import com.farhanadi.moodbotapp.view.other.ChatDay
import com.farhanadi.moodbotapp.view.other.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        setupAction()
        fetchAndDisplayChatData()
        return binding.root
    }

    private fun setupAction() {
        binding.fab.setOnClickListener {
            startActivity(Intent(requireActivity(), CameraActivity::class.java))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchAndDisplayChatData()
    }

    private fun fetchAndDisplayChatData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e("Firestore", "User ID is null. User might not be authenticated.")
            return
        }
        Log.d("Firestore", "User ID: $userId")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userDocument = FirebaseFirestore.getInstance().collection("users").document(userId)
                val chatHistoryCollection = userDocument.collection("chatHistory")
                val documents = chatHistoryCollection.get().await()

                Log.d("Firestore", "Documents retrieved: ${documents.size()}")

                val chatDays = mutableListOf<ChatDay>()
                for (document in documents) {
                    val date = document.id
                    Log.d("Firestore", "Processing date document: $date")
                    val messagesSnapshot = document.reference.collection("messages").get().await()
                    val chatMessages = messagesSnapshot.map { message ->
                        val chatMessage = message.toObject(ChatMessage::class.java)
                        Log.d("Firestore", "Message: $chatMessage")
                        chatMessage
                    }
                    Log.d("Firestore", "Date: $date, Messages: ${chatMessages.size}")
                    chatDays.add(ChatDay(date, chatMessages))
                }

                withContext(Dispatchers.Main) {
                    if (chatDays.isEmpty()) {
                        Log.d("Firestore", "No chat days found")
                    }

                    val chatDayAdapter = ChatDayAdapter(chatDays)
                    binding.rvItemchat.adapter = chatDayAdapter
                    binding.rvItemchat.layoutManager = LinearLayoutManager(requireContext())
                }
            } catch (e: Exception) {
                Log.e("Firestore", "Error getting chat history", e)
            }
        }
    }


    class ChatDayAdapter(private val chatDays: List<ChatDay>) :
        RecyclerView.Adapter<ChatDayAdapter.ChatDayViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatDayViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_riwayatchat, parent, false)
            return ChatDayViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatDayViewHolder, position: Int) {
            val chatDay = chatDays[position]
            holder.bind(chatDay)
        }

        override fun getItemCount(): Int = chatDays.size

        inner class ChatDayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
            private val chatMessagesRecyclerView: RecyclerView =
                itemView.findViewById(R.id.rv_itemchat)

            fun bind(chatDay: ChatDay) {
                dateTextView.text = chatDay.date
                val chatMessageAdapter = ChatMessageAdapter(chatDay.messages)
                chatMessagesRecyclerView.adapter = chatMessageAdapter
                chatMessagesRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            }
        }
    }

    class ChatMessageAdapter(private val chatMessages: List<ChatMessage>) :
        RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_riwayatchat, parent, false)
            return ChatMessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
            val chatMessage = chatMessages[position]
            holder.bind(chatMessage)
        }

        override fun getItemCount(): Int = chatMessages.size

        inner class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val senderTextView: TextView = itemView.findViewById(R.id.tvname_bot)
            private val messageTextView: TextView = itemView.findViewById(R.id.tvdeskripsi)
            private val timestampTextView: TextView = itemView.findViewById(R.id.tv_date)
            private val emotionImageView: ImageView = itemView.findViewById(R.id.emotion_icon)

            fun bind(chatMessage: ChatMessage) {
                senderTextView.text = chatMessage.sender
                messageTextView.text = chatMessage.cmessage
                timestampTextView.text = chatMessage.timestamp
                when (chatMessage.emotion) {
                    "happy" -> emotionImageView.setImageResource(R.drawable.happy_emotion)
                    "angry" -> emotionImageView.setImageResource(R.drawable.angry_emotion)
                    "sad" -> emotionImageView.setImageResource(R.drawable.sad_emotion)
                    else -> emotionImageView.setImageResource(R.drawable.neutral_emotion)
                }
            }
        }
    }
}
