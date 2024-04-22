package com.example.belya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.R
import com.example.belya.api.modeApi.UserApiModelItem
import com.example.belya.databinding.RecyclerPersonInCategoryItemBinding
import com.example.belya.model.User

class PersonAdapterByCity(private var listOfPerson: List<UserApiModelItem>) :
    RecyclerView.Adapter<PersonAdapterByCity.ViewHolder>() {
    class ViewHolder(var itemBinding: RecyclerPersonInCategoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: UserApiModelItem) {
            Glide.with(itemBinding.root.context)
                .load(task.imagePath)
                .placeholder(R.drawable.profile_pic)
                .into(itemBinding.personImage)
            itemBinding.personName.text = task.firstName + " " + task.lastName
            itemBinding.city.text = task.city
            itemBinding.job.text = task.job
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = RecyclerPersonInCategoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PersonAdapterByCity.ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return listOfPerson.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfPerson[position])

        holder.itemBinding.cardPerson.setOnClickListener {
            onItemSelectedClickListnner?.onItemSelectedClick(position, listOfPerson[position])
        }
    }

    fun sumbitList(userList: List<UserApiModelItem>) {
        listOfPerson = userList
        notifyDataSetChanged()
    }

    interface OnItemSelectedClick {
        fun onItemSelectedClick(position: Int, task: UserApiModelItem)
    }

    var onItemSelectedClickListnner: OnItemSelectedClick? = null
}