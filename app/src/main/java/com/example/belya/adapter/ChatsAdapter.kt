package com.example.belya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.R
import com.example.belya.databinding.RecyclerChatsListItemBinding
import com.example.belya.model.User

class ChatsAdapter(private var listofChats : MutableList<User>) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    class ViewHolder(val itemBinding: RecyclerChatsListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: User) {
            val fullName = "${task.firstName} ${task.lastName}"
            itemBinding.personNameChat.text = fullName
            itemBinding.personJobChat.text = task.job

            Glide.with(itemBinding.root.context)
                .load(task.imagePath)
                .placeholder(R.drawable.profile_pic)
                .into(itemBinding.personImageChat)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = RecyclerChatsListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return listofChats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listofChats[position])
        holder.itemBinding.personCardChat.setOnClickListener {
            onItemSelectedClickedListnner?.onItemClicked(position,listofChats[position])
        }
    }
    interface OnItemSelectedClick{
        fun onItemClicked(position: Int,task: User)
    }
    var onItemSelectedClickedListnner : OnItemSelectedClick? =null

}