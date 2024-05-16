package com.example.belya.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.RecyclerCategoriesItemLayoutBinding
import com.example.belya.model.CategoriesItem

class CategoriesAdapter( var lisfOfCategories : List<CategoriesItem>) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    class ViewHolder(var itemBinding: RecyclerCategoriesItemLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: CategoriesItem) {
            itemBinding.image.setImageResource(task.image)
            itemBinding.nameJob.text = task.nameOfJob
            itemBinding.countOfJob.text = task.countOfJob
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = RecyclerCategoriesItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(viewBinding)
    }

    override fun getItemCount(): Int {
        return lisfOfCategories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisfOfCategories[position])

        holder.itemBinding.image.setOnClickListener {
            onItemSelectedClickListnner?.onItemSelectedClick(position,lisfOfCategories[position])
        }
    }

    fun updateData(categoryItemList: List<CategoriesItem>) {
        lisfOfCategories = categoryItemList
        notifyDataSetChanged()
    }

    interface OnItemSelectedClick{
        fun onItemSelectedClick(position: Int, task: CategoriesItem)
    }

    var onItemSelectedClickListnner: OnItemSelectedClick? = null
}