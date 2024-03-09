package com.example.belya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
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
            itemBinding.personImageChat.load(task.imagePath){
                crossfade(1)
                placeholder(R.drawable.ic_profileimg)
            }

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