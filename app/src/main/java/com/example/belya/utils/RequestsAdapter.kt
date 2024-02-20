package com.example.belya.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.RecyclerRequestsListItemBinding
import com.example.belya.model.RequestsItem

class RequestsAdapter(private val listOfRequests : List<RequestsItem>) : RecyclerView.Adapter<RequestsAdapter.ViewHolder>() {
    class ViewHolder(var itemBinding: RecyclerRequestsListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: RequestsItem) {
            val fullName =task.userCustomer.firstName+task.userCustomer.lastName
           // itemBinding.requestImage.setImageResource(task.userCustomer.imagePath!!)
            itemBinding.requestName.text = fullName
           itemBinding.requestCity.text = task.userCustomer.city
            itemBinding.requestPrice.text = task.price
            // pending

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = RecyclerRequestsListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return listOfRequests.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfRequests[position])
    }

}