package com.example.belya.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.belya.databinding.RecyclerMostImportantItemLayoutBinding
import com.example.belya.model.ImportantItem

class ImportantAdapter(private val listOfImportant : List<ImportantItem>) : RecyclerView.Adapter<ImportantAdapter.ViewHolderImportant>() {
    class ViewHolderImportant(var itemBinding: RecyclerMostImportantItemLayoutBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(task: ImportantItem) {
            itemBinding.imageMostImportant.setImageResource(task.image)
            itemBinding.nameMostImportant.text = task.nameOfJob
            itemBinding.occTVMostImportant.text = task.countOfJob
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderImportant {
        val viewBinding = RecyclerMostImportantItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolderImportant(viewBinding)
    }

    override fun getItemCount(): Int {
        return listOfImportant.size
    }

    override fun onBindViewHolder(holder: ViewHolderImportant, position: Int) {
        holder.bind(listOfImportant[position])
    }
}