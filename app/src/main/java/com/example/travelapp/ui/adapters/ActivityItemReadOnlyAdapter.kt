package com.example.travelapp.ui.adapters

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.ActivityItemViewModel
import com.example.travelapp.data.models.ActivityItem
import com.example.travelapp.data.models.ItineraryItem
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ActivityItemReadOnlyAdapter(val context: Context,
    val viewModel: ActivityItemViewModel,
    val uid: String,
    val scheduleId: String,
    val itineraryId: String,
    val itineraryItem: ItineraryItem
)
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
        holder.menu.setOnClickListener(View.OnClickListener {
            popupMenus(it, holder.adapterPosition)
        })
        return holder
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.time.text =
            SimpleDateFormat("HH:mm").format(currentItem.time.toDate())
        holder.name.setText(currentItem.name)




    }

    private fun popupMenus(view: View, adapterPosition: Int){
        var activityList = currentList.toMutableList()
        val positionItem = activityList[adapterPosition]
        val popupMenus = PopupMenu(context, view)
        popupMenus.inflate(R.menu.day_item_menu)
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.delete_item -> {
                    AlertDialog.Builder(context)
                        .setTitle("Delete Activity")
                        .setMessage("Are you sure you want to delete this activity?")
                        .setPositiveButton("Yes"){dialog, _ ->
                            if (activityList.size == 1){
                                Toast.makeText(context, "Cannot delete the last activity", Toast.LENGTH_SHORT).show()

                            }
                            else {
                                activityList.removeAt(adapterPosition)
                                submitList(activityList)
                                notifyItemRemoved(adapterPosition)
                                viewModel.deleteActivity(uid, scheduleId, itineraryId, positionItem)
                                dialog.dismiss()
                            }
                        }
                        .setNegativeButton("No"){dialog, _ -> dialog.dismiss()}
                        .create()
                        .show()

                }
                R.id.edit_item -> {
                    var timestampTemp = activityList[adapterPosition].time
                    val v = LayoutInflater.from(context).inflate(R.layout.activity_day_item, null)
                    val editActivityName = v.findViewById<EditText>(R.id.edit_text_description)
                    editActivityName.setText(activityList[adapterPosition].name)
                    val editTime = v.findViewById<TextView>(R.id.text_view_timer)
                    editTime.text = SimpleDateFormat("HH:mm").format(activityList[adapterPosition].time.toDate())
                    editTime.setOnClickListener(View.OnClickListener {
                        var calendar = Calendar.getInstance()
                        var hour = calendar.get(Calendar.HOUR_OF_DAY)
                        var minute = calendar.get(Calendar.MINUTE)
                        var timePickerDialog = TimePickerDialog(context,
                            TimePickerDialog.OnTimeSetListener {
                                    view, hourOfDay, minute ->
                                timestampTemp = Timestamp(Date(itineraryItem.date.toDate().time +
                                        hourOfDay * 60 * 60 * 1000 + minute * 60 * 1000))
                                editTime.text = SimpleDateFormat("HH:mm").format(timestampTemp.toDate())

                            }, hour, minute, true)
                        timePickerDialog.show()
                    })
                    AlertDialog.Builder(context)
                        .setView(v)
                        .setPositiveButton("Save"){dialog, _ ->
                            if (editActivityName.text.toString() == ""){
                                Toast.makeText(context, "Activity name cannot be empty", Toast.LENGTH_SHORT).show()
                                return@setPositiveButton
                            }
                            else {
                                val activityName = editActivityName.text.toString()
                                val time = editTime.text.toString()
                                val activityItem = ActivityItem(
                                    activityList[adapterPosition].id,
                                    activityName,
                                    timestampTemp
                                )
                                activityList[adapterPosition] = activityItem
                                submitList(activityList)
                                dialog.dismiss()
                            }
                        }
                        .setNegativeButton("Cancel"){dialog, _ ->dialog.dismiss()}
                        .create()
                        .show()
                }
            }
            true
        }
        popupMenus.show()
        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true
        val menu = popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
    }


}