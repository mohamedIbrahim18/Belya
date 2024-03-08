package com.example.belya.ui.customer_main.tabs.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belya.Constant
import com.example.belya.HorizontalItemDecoration
import com.example.belya.databinding.FragmentChatCustomerBinding
import com.example.belya.model.User
import com.example.belya.utils.ChatsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ChatCustomerFragment : Fragment() {
    lateinit var chatsAdapter: ChatsAdapter
    private lateinit var listofChats: MutableList<User>
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
        listofChats = mutableListOf()
        initViews()
        initRecyclerViewForChats()
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

    private fun fetchUserDetails(userId: String) {
        FirebaseFirestore.getInstance().collection(Constant.USER)
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    listofChats.add(it)
                    chatsAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("CHAT_CUSTOMER", "Error fetching user details: ${exception.message}", exception)
            }
    }

    private fun initRecyclerViewForChats() {
        chatsAdapter = ChatsAdapter(listofChats)
        viewBinding.recyclerViewChats.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = chatsAdapter
            chatsAdapter.onItemSelectedClickedListnner = object : ChatsAdapter.OnItemSelectedClick {
                override fun onItemClicked(position: Int, task: User) {
                   /*
                    val bundle = Bundle()
                    bundle.putParcelable("CHAT_DATA", task)
                    findNavController().navigate(
                        R.id.action_chatTechnicianFragment2_to_specificChatActivity,
                        bundle
                    )
                    */
                }
            }
        }
    }
}
