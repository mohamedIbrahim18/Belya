package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.RecyclerPersonInCategoryItemBinding
import com.example.belya.ui.customer_main.tabs.home.categories.adapter.CategoriesItem

class PersonAdapter(private val listOfPerson: List<PersonItem>) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
    class ViewHolder(var itemBinding: RecyclerPersonInCategoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: PersonItem) {
            itemBinding.personImage.setImageResource(task.image)
            itemBinding.personName.text = task.name
            itemBinding.personJob.text = task.typeJob
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfPerson[position])


    }

    interface OnItemSelectedClick {
        fun onItemSelectedClick(position: Int, task: CategoriesItem)
    }

    var onItemSelectedClickListnner: OnItemSelectedClick? = null
}
