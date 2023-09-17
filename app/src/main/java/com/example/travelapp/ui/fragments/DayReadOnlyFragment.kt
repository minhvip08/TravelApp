package com.example.travelapp.ui.fragments

import android.app.TimePickerDialog
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
import com.example.travelapp.data.ActivityItemViewModel
import com.example.travelapp.data.ItineraryViewModel
import com.example.travelapp.data.models.ActivityItem
import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.data.repository.ActivityItemRepository
import com.example.travelapp.data.repository.ItineraryRepository
import com.example.travelapp.ui.adapters.ActivityItemReadOnlyAdapter
import com.example.travelapp.ui.util.RandomString
import com.example.travelapp.ui.util.UiState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DayReadOnlyFragment : Fragment() {
    private lateinit var scheduleId: String
    private lateinit var itineraryId: String
    private lateinit var itineraryItem: ItineraryItem
    private lateinit var addBtn: FloatingActionButton
    var timestampTemp = Timestamp(Date())

    var adapter: ActivityItemReadOnlyAdapter? = null
    private val viewModel = ActivityItemViewModel(
        ActivityItemRepository(
            FirebaseFirestore.getInstance()
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scheduleId = it.getString(SCHEDULE_ID).toString()
            itineraryId = it.getString(ITINERARY_ID).toString()
            itineraryItem = it.getParcelable("itineraryItem")!!

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day_read_only, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set find id
        addBtn = view.findViewById<FloatingActionButton>(R.id.add_task_button)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_day_read_only)

        adapter = ActivityItemReadOnlyAdapter(requireContext(), viewModel, Firebase.auth.currentUser!!.uid, scheduleId, itineraryId, itineraryItem)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        timestampTemp = Timestamp(Date(itineraryItem.date.toDate().time))
        viewModel.getActivities(Firebase.auth.currentUser!!.uid, scheduleId, itineraryId).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    Log.d("DayReadOnlyFragment", "Loading")
                }
                is UiState.Success -> {
                    Log.d("DayReadOnlyFragment", "Success")
                    adapter!!.submitList(it.data)




                }
                is UiState.Failure -> {
                    Log.w("DayReadOnlyFragment", "Failure")
                }
            }
        }


        // Set dialog to add activity
        addBtn.setOnClickListener {
            addActivity()
        }
    }


    fun addActivity(){
        val inflater = LayoutInflater.from(context)
        var activityList = adapter?.currentList?.toMutableList()
        val view = inflater.inflate(R.layout.dialog_add_day_item, null)
        // set view
        val timeEditText = view.findViewById<TextView>(R.id.time_edit_text)
        val activityEditText = view.findViewById<EditText>(R.id.activity_edit_text)


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
            if (activityEditText.text.toString() == "") {
                Toast.makeText(context, "Please enter activity name", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            else if (timeEditText.text.toString() == "") {
                Toast.makeText(context, "Please enter time", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            else {
                val time = timeEditText.text.toString()
                val activityName = activityEditText.text.toString()
                val activityItem = ActivityItem(
                    RandomString.randomString(20), activityName,
                    timestampTemp
                )
                activityList!!.add(activityItem)
                adapter!!.submitList(activityList)
                dialog.dismiss()
            }
        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()



    }

    override fun onStop() {
        super.onStop()
        adapter?.currentList?.forEach {
            viewModel.setActivity(Firebase.auth.currentUser!!.uid, scheduleId, itineraryId, it)
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment ActivityListFragment.
         */
        private const val SCHEDULE_ID = "scheduleId"
        private const val ITINERARY_ID = "itineraryId"
        @JvmStatic
        fun newInstance(scheduleId: String, itineraryId: String, itineraryItem: ItineraryItem) =
            DayReadOnlyFragment().apply {
                arguments = Bundle().apply {
                    putString(SCHEDULE_ID, scheduleId)
                    putString(ITINERARY_ID, itineraryId)
                    putParcelable("itineraryItem", itineraryItem)
                }
            }
    }
}