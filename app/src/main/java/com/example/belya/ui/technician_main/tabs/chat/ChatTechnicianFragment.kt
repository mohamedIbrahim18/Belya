package com.example.belya.ui.technician_main.tabs.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.belya.HorizontalItemDecoration
import com.example.belya.R
import com.example.belya.databinding.FragmentChatTechnicianBinding
import com.example.belya.model.User
import com.example.belya.utils.ChatsAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ChatTechnicianFragment : Fragment() {
    private lateinit var userDetails: User
    lateinit var chatsAdapter: ChatsAdapter
    private lateinit var listofChats: MutableList<User>

    lateinit var viewBinding: FragmentChatTechnicianBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentChatTechnicianBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listofChats = mutableListOf()
        // Fetch data from Firestore
        fetchDataFromFirestore()
    }

    private fun initRecyclerViewForChats() {
        chatsAdapter = ChatsAdapter(listofChats)
        viewBinding.recyclerViewChats.apply {
            addItemDecoration(HorizontalItemDecoration())
            adapter = chatsAdapter
            // when user click on card
            chatsAdapter.onItemSelectedClickedListnner = object : ChatsAdapter.OnItemSelectedClick {
                override fun onItemClicked(position: Int, task: User) {
                    val bundle = Bundle()
                    bundle.putParcelable("CHAT_DATA", task)
                    findNavController().navigate(
                        R.id.action_chatTechnicianFragment2_to_specificChatFragment,
                        bundle
                    )
                }
            }
        }
    }

    private fun fetchDataFromFirestore() {
        val currentUID = FirebaseAuth.getInstance().uid
        if (currentUID != null) {
            FirebaseFirestore.getInstance().collection("chats").document(currentUID)
                .collection("acceptedOffers").get()
                .addOnSuccessListener { documents ->
                    // Clear the list before adding new data
                    listofChats.clear()
                    for (document in documents) {
                        val user = document.toObject(User::class.java)
                        listofChats.add(user)
                    }
                    // Initialize RecyclerView after fetching data
                    initRecyclerViewForChats()
                }
                .addOnFailureListener { exception ->
                    Snackbar.make(
                        viewBinding.root,
                        "Error fetching data: ${exception.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
        }
    }
}