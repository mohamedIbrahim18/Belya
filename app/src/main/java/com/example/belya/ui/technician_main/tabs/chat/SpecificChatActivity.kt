package com.example.belya.ui.technician_main.tabs.chat

import MessagesAdapter
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.ActivitySpecificChatBinding
import com.example.belya.model.Message
import com.example.belya.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SpecificChatActivity : AppCompatActivity() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessagesAdapter
    private lateinit var viewBinding: ActivitySpecificChatBinding
    private lateinit var messageEditText: EditText

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySpecificChatBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Initialize views
        messageRecyclerView = viewBinding.recyclerChatTwoPersons
        messageEditText = viewBinding.message

        // Set up RecyclerView
        messageRecyclerView.layoutManager = LinearLayoutManager(this)

        // Get User object from arguments
        val user = intent.getParcelableExtra<User>("CHAT_DATA")
        val chatId = user?.userID

        if (!chatId.isNullOrBlank()) {
            loadMessagesFromFirestore(chatId)
        } else {
            // Handle missing chatId
            Log.e("SpecificChatActivity", "Chat ID is missing")
        }

        // Set up click listener for send button
        viewBinding.sendBtn.setOnClickListener {
            sendMessage(chatId)
        }
    }

    private fun loadMessagesFromFirestore(chatId: String?) {
        if (!chatId.isNullOrBlank()) {
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle error (e.g., show error message)
                        Log.e("SpecificChatActivity", "Error fetching messages", exception)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val messages = snapshot.toObjects(Message::class.java)
                        displayMessages(messages)
                    } else {
                        // Handle empty data (e.g., show no messages message)
                        Log.d("SpecificChatActivity", "No messages found")
                    }
                }
        } else {
            Log.e("SpecificChatActivity", "Invalid chat ID")
        }
    }

    private fun displayMessages(messages: List<Message>) {
        messageAdapter = MessagesAdapter(messages, currentUser?.uid ?: "")
        messageRecyclerView.adapter = messageAdapter
    }

    private fun sendMessage(chatId: String?) {
        val messageText = messageEditText.text.toString().trim()
        if (messageText.isNotEmpty() && !chatId.isNullOrBlank()) {
            val timestamp = Timestamp.now()

            val message = Message(
                id = UUID.randomUUID().toString(), // Generate a unique ID
                content = messageText,
                senderId = currentUser?.uid ?: "",
                timestamp = timestamp // Use Timestamp.now() for current timestamp
            )

            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    // Message sent successfully
                    messageEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    // Handle failure (e.g., show error message)
                    Log.e("SpecificChatActivity", "Failed to send message", e)
                }
        }
    }
}
