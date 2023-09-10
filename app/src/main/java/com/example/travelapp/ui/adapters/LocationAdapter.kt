package com.example.travelapp.ui.adapters

import android.content.Context
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
import com.example.travelapp.databinding.FragmentHomeBinding
import com.example.travelapp.ui.LocationDetailActivity
import com.example.travelapp.ui.fragments.HomeFragment
import com.google.android.material.imageview.ShapeableImageView

class LocationAdapter :
        ListAdapter<LocationItem, LocationAdapter.MyViewHolder>(LocationItemDiff()){
    lateinit var context: Context
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgLocation: ShapeableImageView = itemView.findViewById(R.id.image_location)
        val locationText: TextView = itemView.findViewById(R.id.popular_location_name)
        val locationAttraction: TextView = itemView.findViewById(R.id.popular_location_count)
        companion object {
            fun create(parent: ViewGroup): MyViewHolder {
                if(parent.id == R.id.recycler_view_search){
                    val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.search_location_item, parent, false)
                    return MyViewHolder(view)
                }
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
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder.create(parent)



    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = getItem(position)

        // get image from drawable
        var idImage = context.resources.getIdentifier(currentItem.image,
            "drawable", context.packageName) // R.drawable.image_name
        holder.imgLocation.setImageResource(idImage)
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