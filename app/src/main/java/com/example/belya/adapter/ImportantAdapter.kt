package com.example.belya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.R
import com.example.belya.api.modeApi.UserApiModel
import com.example.belya.api.modeApi.UserApiModelItem
import com.example.belya.databinding.RecyclerMostImportantItemLayoutBinding
import com.example.belya.model.User

class ImportantAdapter(private var userList: List<UserApiModelItem>) : RecyclerView.Adapter<ImportantAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemBinding: RecyclerMostImportantItemLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(user: UserApiModelItem) {
            // Load image using Glide if imagePath is not null
            Glide.with(itemBinding.root.context).load(user.imagePath)
                .placeholder(R.drawable.profile_pic)
                .into(itemBinding.imageMostImportant)

            // Set other user details
            itemBinding.nameMostImportant.text = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}"
            itemBinding.jobMostImportant.text = user.job.orEmpty()
            itemBinding.userRate.rating = user.personRate?.toFloat() ?: 0f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = RecyclerMostImportantItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        userList[position].let { holder.bind(it) }
        holder.itemView.rootView.setOnClickListener {
            onItemSelectedClickListnner?.onItemSelectedClick(position, userList[position])
        }
    }

    fun submitList(users: List<UserApiModelItem>) {
        userList = users
        notifyDataSetChanged()
    }

    interface OnItemSelectedClick {
        fun onItemSelectedClick(position: Int, task: UserApiModelItem)
    }

    var onItemSelectedClickListnner: OnItemSelectedClick? = null
}
