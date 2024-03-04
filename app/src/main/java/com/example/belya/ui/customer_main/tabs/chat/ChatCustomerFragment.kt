package com.example.belya.ui.customer_main.tabs.chat

import MessagesAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.R
import com.example.belya.databinding.FragmentChatCustomerBinding
import com.example.belya.model.Message
import com.example.belya.ui.technician_main.tabs.chat.SpecificChatActivity
import com.google.firebase.firestore.FirebaseFirestore


class ChatCustomerFragment : Fragment() {
    private lateinit var viewBinding: FragmentChatCustomerBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewBinding = FragmentChatCustomerBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.clickkk.setOnClickListener {
            val intent = Intent(activity, SpecificChatActivity::class.java)
            startActivity(intent)
        }
    }





    }
