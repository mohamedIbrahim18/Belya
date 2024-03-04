package com.example.belya.model

import com.google.firebase.Timestamp
import java.util.Date

data class Message(
    val id: String = "",
    val content: String = "",
    val senderId: String = "",
    val timestamp: Timestamp = Timestamp.now()
)