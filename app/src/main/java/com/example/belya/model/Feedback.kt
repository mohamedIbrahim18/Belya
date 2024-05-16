package com.example.belya.model

import com.google.firebase.Timestamp


data class Feedback(
    val userName: String,
    val imagePath: String, // Assuming you have image resource IDs for user images
    val message: String,
    val rating: Float,
    val time: Timestamp?
)
