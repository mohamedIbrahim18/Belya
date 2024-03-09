package com.example.belya.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.ChatMessageRecyclerRowBinding
import com.example.belya.model.ChatMessageModel
import com.example.belya.utils.AndroidUtils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MessagesAdapter(options: FirestoreRecyclerOptions<ChatMessageModel>, private val context: Context) :
    FirestoreRecyclerAdapter<ChatMessageModel, MessagesAdapter.ChatModelViewHolder>(options) {

    override fun onBindViewHolder(holder: ChatModelViewHolder, position: Int, model: ChatMessageModel) {
        Log.i("haushd", "asjd")
        if (model.senderId == AndroidUtils.currentUserId()) {
            holder.binding.leftChatLayout.visibility = View.GONE
            holder.binding.rightChatLayout.visibility = View.VISIBLE
            holder.binding.rightChatTextview.text = model.message
        } else {
            holder.binding.rightChatLayout.visibility = View.GONE
            holder.binding.leftChatLayout.visibility = View.VISIBLE
            holder.binding.leftChatTextview.text = model.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val binding = ChatMessageRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatModelViewHolder(binding)
    }

    inner class ChatModelViewHolder(val binding: ChatMessageRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root)
}