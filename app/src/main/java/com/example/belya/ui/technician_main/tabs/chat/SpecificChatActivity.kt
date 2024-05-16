package com.example.belya.ui.technician_main.tabs.chat

import android.content.Intent
import android.net.Uri
import com.example.belya.adapter.MessagesAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.Constant
import com.example.belya.R
import com.example.belya.databinding.ActivitySpecificChatBinding
import com.example.belya.model.ChatMessageModel
import com.example.belya.model.ChatroomModel
import com.example.belya.model.User
import com.example.belya.utils.AndroidUtils
import com.example.belya.utils.base.Common
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SpecificChatActivity : AppCompatActivity() {
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var viewBinding: ActivitySpecificChatBinding
    private lateinit var chatroomModel: ChatroomModel
    private lateinit var otherUser: User
    private val currentUserId: String = FirebaseAuth.getInstance().uid!!
    private lateinit var chatroomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySpecificChatBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val check = Common()
        if (!check.isConnectedToInternet(this)){
            check.showInternetDisconnectedDialog(this)
        }
        otherUser = AndroidUtils.getUserModelFromIntent(intent)
        chatroomId = getChatroomID(currentUserId, otherUser.userID)
        viewBinding.cardNameToChat.text = otherUser.firstName + " " + otherUser.lastName
        viewBinding.sendBtn.setOnClickListener {
            val message = viewBinding.message.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessageToUser(message)
            }
        }
        viewBinding.cardLocationToChat.setOnClickListener {
            val intent = Intent(this,MapsActivity::class.java)
            intent.putExtra("otherUserId", otherUser.userID) // Pass the ID of the other user
            startActivity(intent)
        }


        getOrCreateChatRoomModel()
        setupChatRecyclerView()
        setupPhoneAndImage(otherUser)
    }

    private fun setupPhoneAndImage(otherUser: User) {
        viewBinding.cardPhoneToChat.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${otherUser.phoneNumber}")
            startActivity(intent)
        }

        // Retrieve the user data from Firestore
        val firestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection(Constant.USER).document(otherUser.userID)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        // Load and display the user's profile image using Glide
                        Glide.with(this)
                            .load(it.imagePath)
                            .placeholder(R.drawable.profile_pic) // Placeholder image while loading
                            .into(viewBinding.cardImageToChat)
                    }
                } else {
                    // Handle the case where the document doesn't exist
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }


    private fun setupChatRecyclerView() {
        val query = getChatRoomMessageRef(chatroomId)
            .orderBy("timeStamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>()
            .setQuery(query, ChatMessageModel::class.java)
            .build()

        messagesAdapter = MessagesAdapter(options, applicationContext)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        viewBinding.recyclerChatTwoPersons.layoutManager = manager
        viewBinding.recyclerChatTwoPersons.adapter = messagesAdapter
        messagesAdapter.startListening()

        messagesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                manager.smoothScrollToPosition(viewBinding.recyclerChatTwoPersons, null, 0)
            }
        })

    }

    private fun sendMessageToUser(message: String) {
        chatroomModel.lastMessageTimeStamp = Timestamp.now()
        chatroomModel.lastMessageSenderId = currentUserId
        getChatRoomRef(chatroomId).set(chatroomModel)

        val chatMessageModel = ChatMessageModel(message, currentUserId, Timestamp.now())
        getChatRoomMessageRef(chatroomId).add(chatMessageModel).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                viewBinding.message.text.clear()
            }
        }
    }

    private fun getOrCreateChatRoomModel() {
        getChatRoomRef(chatroomId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    chatroomModel = document.toObject(ChatroomModel::class.java)!!
                } else {
                    // Create a new chatroom model if it doesn't exist
                    chatroomModel = ChatroomModel(
                        chatroomId,
                        listOf(currentUserId, otherUser.userID),
                        Timestamp.now(),
                        ""
                    )
                    getChatRoomRef(chatroomId).set(chatroomModel)
                }
            } else {
                Log.e("ERror", "Error getting chatroom document", task.exception)
            }
        }
    }

    private fun getChatRoomRef(chatRoomId: String): DocumentReference {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId)
    }

    private fun getChatRoomMessageRef(chatRoomId: String): CollectionReference {
        return getChatRoomRef(chatRoomId).collection("chats")
    }

    private fun getChatroomID(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            userId1 + "_" + userId2
        } else {
            userId2 + "_" + userId1
        }
    }

}