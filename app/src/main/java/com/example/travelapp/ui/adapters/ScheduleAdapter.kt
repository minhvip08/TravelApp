package com.example.travelapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.ScheduleItem

class ScheduleAdapter :
    ListAdapter<ScheduleItem, ScheduleAdapter.ViewHolder>(ScheduleItemDiff()) {
    
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.schedule_name)
        val image: ImageView = itemView.findViewById(R.id.schedule_image)
        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.schedule_item, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class ScheduleItemDiff : DiffUtil.ItemCallback<ScheduleItem>() {
        override fun areItemsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ScheduleItem, newItem: ScheduleItem): Boolean {
            return oldItem.name == newItem.name && oldItem.startDate == newItem.startDate
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder.create(parent)
        holder.image.clipToOutline = true
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.name.text = currentItem.name
    }
}