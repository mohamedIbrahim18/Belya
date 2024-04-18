package com.example.belya.ui.customer_main.tabs.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.databinding.FragmentChatCustomerBinding
import com.example.belya.model.User
import com.example.belya.ui.technician_main.tabs.chat.SpecificChatActivity
import com.example.belya.adapter.ChatsAdapter
import com.example.belya.utils.AndroidUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatCustomerFragment : Fragment() {
    private lateinit var chatsAdapter: ChatsAdapter
    private val listOfChats: MutableList<User> = mutableListOf()
    private lateinit var viewBinding: FragmentChatCustomerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentChatCustomerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewForChats()
        initViews()
    }

    private fun initViews() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            FirebaseFirestore.getInstance().collection(Constant.USER)
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        Log.e("CHAT_CUSTOMER", "Error fetching users: ${exception.message}", exception)
                        return@addSnapshotListener
                    }

                    querySnapshot?.documents?.forEach { document ->
                        val acceptedList = document.get("acceptedList") as? List<String>
                        if (acceptedList != null && currentUserId in acceptedList) {
                            val userId = document.id
                            fetchUserDetails(userId)
                        }
                    }
                }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUserDetails(userId: String) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    listOfChats.add(it)
                    chatsAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CHAT_CUSTOMER", "Error fetching user details: ${exception.message}", exception)
            }
    }

    private fun initRecyclerViewForChats() {
        chatsAdapter = ChatsAdapter(listOfChats)
        viewBinding.recyclerViewChats.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(HorizontalItemDecoration())
            adapter = chatsAdapter
            chatsAdapter.onItemSelectedClickedListnner = object : ChatsAdapter.OnItemSelectedClick {
                override fun onItemClicked(position: Int, task: User) {
                    val intent = Intent(context, SpecificChatActivity::class.java)
                    AndroidUtils.passUserModelAsIntent(intent,task)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
        }
    }
}
