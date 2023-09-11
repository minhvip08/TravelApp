package com.example.travelapp.ui.fragments

import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.ActivityItem
import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.ui.adapters.DayAdapter
import com.example.travelapp.ui.util.RandomString
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.database.core.Constants
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var addBtn: FloatingActionButton
    private lateinit var recv: RecyclerView
    private lateinit var dayAdapter: DayAdapter
    var itineraryItem: ItineraryItem = ItineraryItem()
    private lateinit var timerTextView: TextView
    var activityList: ArrayList<ActivityItem> = ArrayList<ActivityItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        var locationItem = arguments?.getParcelable<LocationItem>("location")
        var scheduleItem = arguments?.getParcelable<ScheduleItem>("schedule")
        var day = arguments?.getInt("day")

        itineraryItem.date= Timestamp(Date(scheduleItem!!.startDate.toDate().time
                + (day!!-1) * 24 * 60 * 60 * 1000))
        Log.d(TAG, "onCreate date: ${itineraryItem.date.toDate()}")
        itineraryItem.id = RandomString.randomString(20)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set find id
        addBtn = view.findViewById<FloatingActionButton>(R.id.add_task_button)
        recv = view.findViewById<RecyclerView>(R.id.itinerary_recycler_view)
        //set adapter
        dayAdapter = DayAdapter(requireContext(),  activityList, itineraryItem)
        recv.adapter = dayAdapter
        //set layout manager
        recv.setHasFixedSize(true)
        recv.layoutManager = LinearLayoutManager(requireContext())
        // Set dialog to add activity
        addBtn.setOnClickListener {
            addActivity()
        }

    }

    override fun onStart() {
        super.onStart()

    }

    fun addActivity(){
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_add_day_item, null)
        // set view
        val timeEditText = view.findViewById<TextView>(R.id.time_edit_text)
        val activityEditText = view.findViewById<EditText>(R.id.activity_edit_text)
        var timestampTemp = itineraryItem.date


        // set time picker
        timeEditText.setOnClickListener {
            var calendar = Calendar.getInstance()
            var hour = calendar.get(Calendar.HOUR_OF_DAY)
            var minute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    timestampTemp = Timestamp(Date(itineraryItem.date.toDate().time + hourOfDay * 60 * 60 * 1000 + minute * 60 * 1000))
                    timeEditText.text = SimpleDateFormat("HH:mm").format(timestampTemp.toDate())
                },
                hour,
                minute,
                true
            )
            timePickerDialog.show()
        }

        val addDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(view)
        addDialog.setPositiveButton("Add") { dialog, _ ->
            val time = timeEditText.text.toString()
            val activityName = activityEditText.text.toString()
            val activityItem = ActivityItem(RandomString.randomString(20), activityName,
                timestampTemp)
            activityList.add(activityItem)
            dayAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ItineraryActivityFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}