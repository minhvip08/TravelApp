package com.example.travelapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.LocationItem

class LocationAdapter () :
        RecyclerView.Adapter<LocationAdapter.MyViewHolder>(){
    private var locationList: MutableList<LocationItem> = arrayListOf()
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imgLocation: ImageView = itemView.findViewById(R.id.image_location)
        var locationText: TextView = itemView.findViewById(R.id.popular_location_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.popular_location_item,
            parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = locationList[position]

        holder.imgLocation.setImageResource(R.drawable.switzerland)
        holder.locationText.text = currentItem.title

    }

    fun updateList(list: MutableList<LocationItem>){
        this.locationList = list
        notifyDataSetChanged()
    }
    fun removeItem(position: Int){
        locationList.removeAt(position)
        notifyItemChanged(position)
    }
}