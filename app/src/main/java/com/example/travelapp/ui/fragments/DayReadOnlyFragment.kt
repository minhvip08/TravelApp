package com.example.travelapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.ActivityItemViewModel
import com.example.travelapp.data.models.ActivityItem
import com.example.travelapp.data.repository.ActivityItemRepository
import com.example.travelapp.ui.adapters.ActivityItemReadOnlyAdapter
import com.example.travelapp.ui.util.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class DayReadOnlyFragment : Fragment() {
    private lateinit var scheduleId: String
    private lateinit var itineraryId: String
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
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_day_read_only)
        adapter = ActivityItemReadOnlyAdapter(requireContext(), viewModel, Firebase.auth.currentUser!!.uid, scheduleId, itineraryId)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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
        fun newInstance(scheduleId: String, itineraryId: String) =
            DayReadOnlyFragment().apply {
                arguments = Bundle().apply {
                    putString(SCHEDULE_ID, scheduleId)
                    putString(ITINERARY_ID, itineraryId)
                }
            }
    }
}