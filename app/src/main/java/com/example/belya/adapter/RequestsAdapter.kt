package com.example.belya.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.R
import com.example.belya.databinding.RecyclerRequestsListItemBinding
import com.example.belya.model.User

class RequestsAdapter(private var listOfRequests : List<User>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {
    class ViewHolder(val itemBinding: RecyclerRequestsListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: User) {
            val fullName = "${task.firstName} ${task.lastName}"
            itemBinding.requestName.text = fullName
            itemBinding.requestCity.text = task.city
            itemBinding.description.text = task.description
            itemBinding.requestPrice.text = task.price.trim()

            Glide.with(itemBinding.root.context).load(task.imagePath)
                .placeholder(R.drawable.profile_pic)
                .into(itemBinding.requestImage)

            Log.d("price in adapter",task.price.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = RecyclerRequestsListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return listOfRequests.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = listOfRequests[position]
        holder.bind(currentUser)
        holder.itemBinding.requestRejectBtn.setOnClickListener {
            onItemRejectedClickListnner?.onItemRejectedClick(position, listOfRequests[position])
        }
        holder.itemBinding.requestAceeptBtn.setOnClickListener {
            onItemSelectedClickListnner?.onItemSelectedClick(position,listOfRequests[position])
        }
    }
    interface OnItemSelectedClick {
        fun onItemSelectedClick(position: Int, task: User)
    }
    interface OnItemRejectedClick{
        fun onItemRejectedClick(position: Int, task: User)

    }

    var onItemSelectedClickListnner: OnItemSelectedClick? = null
    var onItemRejectedClickListnner : OnItemRejectedClick?=null
}