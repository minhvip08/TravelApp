package com.example.travelapp.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.ImageViewModel
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.data.repository.ImageRepository
import com.example.travelapp.ui.fragments.ItineraryFragment
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class ScheduleAdapter(
    private val supportFragmentManager: FragmentManager,
    private val isHistory: Boolean
) : ListAdapter<ScheduleItem, ScheduleAdapter.ViewHolder>(ScheduleItemDiff()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.schedule_name)
        val image: ImageView = itemView.findViewById(R.id.schedule_image)
        var id: String = ""
        val date: TextView = itemView.findViewById(R.id.schedule_date)
        val hotelName: TextView = itemView.findViewById(R.id.schedule_hotel_name)
        val cost: TextView = itemView.findViewById(R.id.schedule_cost)
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
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder.create(parent)
        holder.image.clipToOutline = true
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem.image != "") {
            imageViewModel.getImagePath(currentItem.image) {
                CoroutineScope(Dispatchers.IO).launch {
                    val bitmap = BitmapFactory.decodeFile(it)
                    withContext(Dispatchers.Main) {
                        holder.image.setImageBitmap(bitmap)
                    }
                }
            }
        }
        holder.name.text = holder.itemView.context.getString(R.string.schedule_hotel_name, currentItem.name)
        holder.id = currentItem.id
        holder.date.text = holder.itemView.context.getString(
            R.string.schedule_date,
            SimpleDateFormat("dd/MM/yyyy").format(currentItem.startDate.toDate()),
            SimpleDateFormat("dd/MM/yyyy").format(currentItem.endDate.toDate())
        )
        holder.hotelName.text = currentItem.hotelName
        holder.cost.text = holder.itemView.context.getString(R.string.schedule_cost, currentItem.cost)
        if (!isHistory) {
            holder.itemView.setOnClickListener {
                supportFragmentManager.commit {
                    replace(
                        R.id.schedule_wrapper,
                        ItineraryFragment.newInstance(currentItem.id)
                    )
                    addToBackStack(null)
                }
            }
        }
    }

    companion object {
        private val imageViewModel = ImageViewModel(
            ImageRepository(
                FirebaseStorage.getInstance().reference
            )
        )
    }
}