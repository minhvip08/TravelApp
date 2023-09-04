package com.example.travelapp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.ui.LocationDetailActivity

class LocationAdapter :
        ListAdapter<LocationItem, LocationAdapter.MyViewHolder>(LocationItemDiff()){
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgLocation: ImageView = itemView.findViewById(R.id.image_location)
        val locationText: TextView = itemView.findViewById(R.id.popular_location_name)
        val locationAttraction: TextView = itemView.findViewById(R.id.popular_location_count)
        companion object {
            fun create(parent: ViewGroup): MyViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.popular_location_item, parent, false)
                return MyViewHolder(view)
            }
        }
    }

    class LocationItemDiff : DiffUtil.ItemCallback<LocationItem>() {
        override fun areItemsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
            return oldItem.title == newItem.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = getItem(position)

        holder.imgLocation.setImageResource(R.drawable.switzerland_banner)
        holder.locationText.text = currentItem.title
        holder.locationAttraction.text = currentItem.attraction.size.toString() + " attractions"

        holder.itemView.setOnClickListener {
            Intent(holder.itemView.context, LocationDetailActivity::class.java).also {
                it.putExtra("location", currentItem)
                holder.itemView.context.startActivity(it)
            }
        }
    }
}