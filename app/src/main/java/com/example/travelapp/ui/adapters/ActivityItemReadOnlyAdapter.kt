package com.example.travelapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.ActivityItem
import java.text.SimpleDateFormat

class ActivityItemReadOnlyAdapter
    : ListAdapter<ActivityItem, ActivityItemReadOnlyAdapter.ViewHolder>(ActivityItemDiff()) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.text_view_timer)
        val name: EditText = itemView.findViewById(R.id.edit_text_description)
        val menu: View = itemView.findViewById(R.id.menu_item_image_view)
        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_day_item, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class ActivityItemDiff : DiffUtil.ItemCallback<ActivityItem>() {
        override fun areItemsTheSame(oldItem: ActivityItem, newItem: ActivityItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ActivityItem, newItem: ActivityItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder.create(parent)
        holder.name.isEnabled = false
        holder.name.setTextColor(
            parent.context.resources.getColorStateList(R.color.edit_text_disabled, null)
        )
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.time.text =
            SimpleDateFormat("HH:mm").format(currentItem.time.toDate())
        holder.name.setText(currentItem.name)
    }

}