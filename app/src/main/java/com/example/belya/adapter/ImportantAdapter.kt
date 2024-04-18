package com.example.belya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.R
import com.example.belya.api.modeApi.UserApiModelItem
import com.example.belya.databinding.RecyclerMostImportantItemLayoutBinding

class ImportantAdapter(private var listOfImportant: List<UserApiModelItem>) : RecyclerView.Adapter<ImportantAdapter.ViewHolderImportant>() {

    inner class ViewHolderImportant(private val itemBinding: RecyclerMostImportantItemLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(user: UserApiModelItem) {
            // Load image with Glide and set placeholder
            Glide.with(itemBinding.root.context)
                .load(user.imagePath)
                .placeholder(R.drawable.ic_profileimg)
                .into(itemBinding.imageMostImportant)

            // Set name
            val fullName = "${user.firstName.orEmpty()} ${user.lastName.orEmpty()}"
            itemBinding.nameMostImportant.text = fullName

            // Set job
            itemBinding.jobMostImportant.text = user.job

            // Set person rate if not null
            user.personRate?.let {
                itemBinding.userRate.rating = it.toFloat()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImportant {
        val viewBinding = RecyclerMostImportantItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderImportant(viewBinding)
    }

    override fun getItemCount(): Int {
        return listOfImportant.size
    }

    override fun onBindViewHolder(holder: ViewHolderImportant, position: Int) {
        holder.bind(listOfImportant[position])
    }

    fun submitList(users: List<UserApiModelItem?>) {
        this.listOfImportant = users.filterNotNull()
        notifyDataSetChanged()
    }
}
