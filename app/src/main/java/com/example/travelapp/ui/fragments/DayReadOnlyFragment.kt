package com.example.travelapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.travelapp.R
import com.example.travelapp.data.models.ItineraryItem

class DayReadOnlyFragment : Fragment() {
    private lateinit var itineraryId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment ActivityListFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val ITINERARY_ID = "activityList"
        @JvmStatic
        fun newInstance(itineraryId: String) =
            DayReadOnlyFragment().apply {
                arguments = Bundle().apply {
                    putString(ITINERARY_ID, itineraryId)
                }
            }
    }
}