package com.example.travelapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.travelapp.R
import com.example.travelapp.data.ItineraryViewModel
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.data.repository.ItineraryRepository
import com.example.travelapp.ui.adapters.ItineraryViewPagerAdapter
import com.example.travelapp.ui.util.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ItineraryFragment : Fragment() {

    private val viewModel = ItineraryViewModel(
        ItineraryRepository(
            FirebaseFirestore.getInstance()
        )
    )

    private lateinit var scheduleId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            scheduleId = it.getString(SCHEDULE_ID).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_itinerary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar_itinerary)
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        viewModel.getItineraries(
            Firebase.auth.currentUser!!.uid,
            scheduleId
        ).observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    Log.d("ItineraryFragment", "Loading")
                }
                is UiState.Success -> {
                    Log.d("ItineraryFragment", "Success")
                    val adapter = ItineraryViewPagerAdapter(childFragmentManager, lifecycle, it.data)
                }
                is UiState.Failure -> {
                    Log.w("ItineraryFragment", it.error!!)
                }
            }
        }
    }

    companion object {
        private const val SCHEDULE_ID = "scheduleId"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param scheduleItem Parameter 1.
         * @return A new instance of fragment ItineraryFragment.
         */
        @JvmStatic
        fun newInstance(scheduleId: String) =
            ItineraryFragment().apply {
                arguments = Bundle().apply {
                    putString(SCHEDULE_ID, scheduleId)
                }
            }
    }

}