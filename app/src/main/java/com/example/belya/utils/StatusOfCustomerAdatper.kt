package com.example.belya.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.R
import com.example.belya.model.StatusOfCustomer

class StatusOfCustomerAdapter(private val statusList: MutableList<StatusOfCustomer> = mutableListOf()) : RecyclerView.Adapter<StatusOfCustomerAdapter.StatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_notification_customer_item, parent, false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        val status = statusList[position]
        holder.bind(status)
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    fun addStatus(statusOfCustomer: StatusOfCustomer) {
        statusList.add(statusOfCustomer)
        notifyDataSetChanged()
    }

    fun updateTime(userId: String, currentTime: String) {
        for (status in statusList) {
            if (status.userName == userId) {
                status.timeNow = currentTime
                notifyDataSetChanged() // Notify the adapter about the data change
                return // Exit the loop once the item is updated
            }
        }
    }

    inner class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val statusTextView: TextView = itemView.findViewById(R.id.status_notification_text_view)
        private val timeTextView: TextView = itemView.findViewById(R.id.time_now)

        fun bind(statusOfCustomer: StatusOfCustomer) {
            statusTextView.text = statusOfCustomer.userName
            timeTextView.text = statusOfCustomer.timeNow
        }
    }
}
