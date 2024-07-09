package com.farhanadi.moodbotapp.view.other

data class Message(
    val message: String,
    val sentBy: String
)

object MessageType {
    const val SENT_BY_USER = "user"
    const val SENT_BY_BOT = "bot"
}