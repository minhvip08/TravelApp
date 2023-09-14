package com.example.travelapp.ui.adapters

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.HotelItem
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.DetailHotelActivity
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HotelAdapter(
    private val hotelList: List<HotelItem>,
    val locationItem: LocationItem,
    val scheduleItem: ScheduleItem) :
    RecyclerView.Adapter<HotelAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHotel = itemView.findViewById<ShapeableImageView>(R.id.image_view_hotel)
        val hotelName = itemView.findViewById<TextView>(R.id.name_hotel)
        val hotelPrice = itemView.findViewById<TextView>(R.id.price_hotel)
        val ratingBar = itemView.findViewById<RatingBar>(R.id.rating_bar_hotel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hotel_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hotelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = hotelList[position]
        holder.hotelPrice.text = "From \$ ${currentItem.price.toString()}"
        holder.hotelName.text = currentItem.title
        holder.ratingBar.rating = currentItem.rating.toFloat()
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailHotelActivity::class.java)
            intent.putExtra("hotel", currentItem)
            intent.putExtra("location", locationItem)
            intent.putExtra("schedule", scheduleItem)
            holder.itemView.context.startActivity(intent)
        }
        if (currentItem.imagePath != "") {
            CoroutineScope(Dispatchers.IO).launch {
                BitmapFactory.decodeFile(currentItem.imagePath)
                withContext(Dispatchers.Main) {
                    holder.imgHotel.setImageBitmap(BitmapFactory.decodeFile(currentItem.imagePath))
                }
            }
        }
    }


}