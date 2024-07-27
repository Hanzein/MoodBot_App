package com.farhanadi.moodbotapp.view.other

data class Message(
    val message: String,
    val sentBy: String
)

data class EmotionHistory(
    val emotion: String,
    val date: String,
    val time: String
)

data class MoodEntry(
    val date: String = "",
    val emotion: String = "",
    val description: String = ""
)


data class ChatDay(
    val date: String = "",
    val messages: List<ChatMessage> = emptyList()
)

data class ChatMessage(
    val sender: String,
    val cmessage: String,
    val timestamp: String,
    val emotion: String
)

object MessageType {
    const val SENT_BY_USER = "user"
    const val SENT_BY_BOT = "bot"
}