package com.example.belya.ui.customer_main.tabs.home.categories.who_in_this_category.who_in_this_category_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.belya.R
import com.example.belya.databinding.RecyclerPersonInCategoryItemBinding
import com.example.belya.model.userTechnician

class PersonAdapter(private var listOfPerson: MutableList<userTechnician>) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
    class ViewHolder(var itemBinding: RecyclerPersonInCategoryItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(task: userTechnician) {
           // itemBinding.personImage.text(task.imagePath)
            itemBinding.personImage.load(task.imagePath){
                crossfade(500)
                placeholder(R.drawable.ic_profileimg)
            }
            itemBinding.personName.text = task.firstName
            itemBinding.personJob.text = task.job
            itemBinding.personRate.rating = task.person_rate.toFloat()
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

    fun newList(mList: MutableList<userTechnician>){
        listOfPerson = mList
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listOfPerson[position])

        holder.itemBinding.cardPerson.setOnClickListener {
            onItemSelectedClickListnner?.onItemSelectedClick(position,listOfPerson[position])
        }


    }

    interface OnItemSelectedClick {
        fun onItemSelectedClick(position: Int, task: userTechnician)
    }

    var onItemSelectedClickListnner: OnItemSelectedClick? = null
}
