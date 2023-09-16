package com.example.travelapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.travelapp.R
import com.example.travelapp.data.ItineraryViewModel
import com.example.travelapp.data.models.ItineraryItem
import com.example.travelapp.data.repository.ItineraryRepository
import com.example.travelapp.ui.adapters.ItineraryViewPagerAdapter
import com.example.travelapp.ui.util.UiState
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
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
    private lateinit var adapter: ItineraryViewPagerAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
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
            parentFragmentManager.popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        tabLayout = view.findViewById(R.id.tab_layout_itinerary)
        viewPager = view.findViewById(R.id.view_pager_itinerary)
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
                    updateUi(it.data)
                }
                is UiState.Failure -> {
                    Log.w("ItineraryFragment", it.error!!)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

    }

    private fun updateUi(itineraries: List<ItineraryItem>) {
        adapter = ItineraryViewPagerAdapter(this, requireContext(), scheduleId, itineraries)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.customView = adapter.getTabView(position)
        }.attach()
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