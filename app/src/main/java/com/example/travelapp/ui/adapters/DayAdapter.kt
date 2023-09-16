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
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.ActivityItem
import com.example.travelapp.data.models.ItineraryItem
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DayAdapter(
    val context: Context,
    val activityList: ArrayList<ActivityItem>,
    val itineraryItem: ItineraryItem):
    RecyclerView.Adapter<DayAdapter.MyViewHolder>() {

    inner class MyViewHolder(val view: View): RecyclerView.ViewHolder(view){
        var time: TextView
        var activityName: EditText
        var mMenus: ImageView
        var timestampTemp = Timestamp(itineraryItem.date.toDate())

        init {
            time = view.findViewById(R.id.text_view_timer)
            activityName = view.findViewById(R.id.edit_text_description)
            mMenus = view.findViewById(R.id.menu_item_image_view)
            mMenus.setOnClickListener { popupMenus(it) }

        }

        private fun popupMenus(view: View){
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
                                activityList.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){dialog, _ -> dialog.dismiss()}
                            .create()
                            .show()
                    }
                    R.id.edit_item -> {
                        timestampTemp = activityList[adapterPosition].time
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
                                    notifyItemChanged(adapterPosition)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_day_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = activityList[position]
        holder.time.text = SimpleDateFormat("HH:mm").format(currentItem.time.toDate())
        holder.activityName.setText(activityList[position].name)
        holder.activityName.focusable = View.NOT_FOCUSABLE
    }
}