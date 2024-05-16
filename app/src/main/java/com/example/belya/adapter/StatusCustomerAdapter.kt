package com.example.belya.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.RecyclerNotificationCustomerItemBinding
import com.example.belya.model.StatusOfCustomer

class StatusCustomerAdapter(private val statusList: MutableList<StatusOfCustomer> = mutableListOf()) :
    RecyclerView.Adapter<StatusCustomerAdapter.ViewHolder>() {

    class ViewHolder(var itemBinding: RecyclerNotificationCustomerItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(statusOfCustomer: StatusOfCustomer) {
            itemBinding.statusNotificationTextView.text = statusOfCustomer.userName
            itemBinding.timeNow.text = statusOfCustomer.timeNow
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val viewBinding = RecyclerNotificationCustomerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(statusList[position])
    }

    override fun getItemCount(): Int {
        return statusList.size
    }
    fun addStatus(statusOfCustomer: StatusOfCustomer) {
        statusList.add(statusOfCustomer)
        notifyDataSetChanged()
    }
//    fun updateTime(formattedTime: String) {
//        for (status in statusList) {
//            status.timeNow = formattedTime
//        }
//        notifyDataSetChanged() // Notify the adapter about the data change
//    }
}