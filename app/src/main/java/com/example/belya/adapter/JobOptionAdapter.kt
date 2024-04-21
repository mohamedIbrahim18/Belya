package com.example.belya.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.belya.R
import com.example.belya.model.JobOption

class JobOptionAdapter(context: Context, private val jobOptions: List<JobOption>) :
    ArrayAdapter<JobOption>(context, 0, jobOptions) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.item_spinner_layout, parent, false)
        }

        val jobNameTextView: TextView = itemView!!.findViewById(R.id.jobName)
        val jobImageView: ImageView = itemView.findViewById(R.id.jobImage)

        val jobOption = jobOptions[position]
        jobNameTextView.text = jobOption.name
        jobImageView.setImageResource(jobOption.imageResId)

        return itemView
    }
}
