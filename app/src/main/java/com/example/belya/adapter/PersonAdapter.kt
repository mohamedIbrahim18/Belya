package com.example.belya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belya.R
import com.example.belya.databinding.RecyclerPersonInCategoryItemBinding
import com.example.belya.model.User

class PersonAdapter(private var listOfPerson: MutableList<User>) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
    class ViewHolder(var itemBinding: RecyclerPersonInCategoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: User) {
            // itemBinding.personImage.text(task.imagePath)
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
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return listOfPerson.size
    }

    fun newList(mList: MutableList<User>) {
        listOfPerson = mList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfPerson[position])

        holder.itemBinding.cardPerson.setOnClickListener {
            onItemSelectedClickListnner?.onItemSelectedClick(position, listOfPerson[position])
        }


    }

    interface OnItemSelectedClick {
        fun onItemSelectedClick(position: Int, task: User)
    }

    var onItemSelectedClickListnner: OnItemSelectedClick? = null
}
