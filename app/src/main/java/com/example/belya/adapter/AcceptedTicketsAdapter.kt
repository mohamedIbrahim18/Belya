package com.example.belya.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.RecyclerAcceptedTicketsItemBinding
import com.example.belya.model.User

class AcceptedTicketsAdapter(private var listOfAcceptedList: List<User>) : RecyclerView.Adapter<AcceptedTicketsAdapter.ViewHolder>() {
    inner class ViewHolder(val itemBinding: RecyclerAcceptedTicketsItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: User) {
            val fullName = "${task.firstName} ${task.lastName}"
            itemBinding.acceptedName.text = fullName
            itemBinding.acceptedCity.text = task.city
            itemBinding.acceptedPrice.text = task.price.trim()
            Log.d("price in adapter", task.price)
            Log.d("city in adapter", task.city!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = RecyclerAcceptedTicketsItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return listOfAcceptedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = listOfAcceptedList[position]
        holder.bind(currentUser)

        holder.itemBinding.finishedTicked.setOnClickListener {
            onItemSelectedClickListener?.onItemSelectedClick(position, listOfAcceptedList[position])
        }
    }

    interface OnItemSelectedClick {
        fun onItemSelectedClick(position: Int, task: User)
    }

    var onItemSelectedClickListener: OnItemSelectedClick? = null
}
