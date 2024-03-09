package com.example.belya.model

import com.google.firebase.Timestamp

data class ChatMessageModel(
    var message:String,
    var senderId : String,
    var timeStamp : Timestamp
){
    constructor() : this("", "", Timestamp.now())
}