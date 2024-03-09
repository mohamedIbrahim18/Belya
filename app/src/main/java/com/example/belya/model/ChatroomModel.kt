package com.example.belya.model

import com.google.firebase.Timestamp

data class ChatroomModel(
 val chatRoomId: String,
 val userIds: List<String?>,
 var lastMessageTimeStamp: Timestamp,
 var lastMessageSenderId: String
){
constructor() : this("", emptyList(), Timestamp.now(),"")
}
