package com.example.travelapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.example.travelapp.R
import com.example.travelapp.data.LocationViewModel
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.databinding.FragmentHomeBinding
import com.example.travelapp.ui.SearchLocationActivity
import com.example.travelapp.ui.UserProfileActivity
import com.example.travelapp.ui.adapters.LocationAdapter
import com.example.travelapp.ui.adapters.ViewPagerTopImagesAdapter
import com.example.travelapp.ui.util.FirebaseStorageConstants
import com.example.travelapp.ui.util.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var locationAdapter: LocationAdapter
    lateinit var locationRecyclerView: RecyclerView
    lateinit var sessionTextView: TextView
    lateinit var userTextView: TextView
    lateinit var searchLocation: SearchView
    val viewModel: LocationViewModel = LocationViewModel(LocationRepository(
        FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
    ))

    // Viewpager Top Images initializes
    lateinit var mSliderViewPager: ViewPager
    lateinit var mDotLayout: LinearLayout
    lateinit var mDots: Array<TextView>
    lateinit var mStartAdapter: ViewPagerTopImagesAdapter



//    var handlerThread = Handler()
//    private val runnable = Runnable {
//        if (mSliderViewPager.currentItem == images.size - 1) {
//            mSliderViewPager.currentItem = 0
//        } else {
//            mSliderViewPager.currentItem = mSliderViewPager.currentItem + 1
//        }
//    }

    var images: ArrayList<Int> = ArrayList()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        binding = FragmentHomeBinding.inflate(layoutInflater)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.text_view_user).text =
            Firebase.auth.currentUser?.displayName ?: getString(R.string.guest)

        locationAdapter = LocationAdapter()

        val recyclerView = activity?.findViewById<RecyclerView>(R.id.popular_location_recyclerview)


        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = locationAdapter

        searchLocation = activity?.findViewById(R.id.search_location)!!
        searchLocation.inputType = InputType.TYPE_NULL
        searchLocation.setOnClickListener {
            val intent = Intent(requireActivity(), SearchLocationActivity::class.java)
            startActivity(intent)
        }

        FirebaseFirestore.setLoggingEnabled(true)

        viewModel.getLocations().observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    Log.d("TAG", "Loading")
                }
                is UiState.Success -> {
                    Log.d("TAG", "Success")
                    val x = it.data
                    for (item in x) {
                        viewModel.getAttractionLocations(item)
                        Log.w("TAG", "ADD ITEM LOCATION")

                    }
                    locationAdapter.submitList(it.data)
                }
                is UiState.Failure -> {
                    Log.d("TAG", it.error!!)
                }

                else -> {
                    Log.d("TAG", "Else")}
            }
        }

        view.findViewById<ImageView>(R.id.image_view_user).setOnClickListener {
            val intent = Intent(requireActivity(), UserProfileActivity::class.java)
            startActivity(intent)
        }

        images.add(R.drawable.vietnam)
        images.add(R.drawable.switzerland)
        userTextView = activity?.findViewById(R.id.text_view_user)!!
        userTextView.text = Firebase.auth.currentUser?.displayName ?: getString(R.string.guest)
        mDotLayout = activity?.findViewById(R.id.dots_layout_top_images)!!
        mSliderViewPager = activity?.findViewById(R.id.top_image_viewpager)!!
        mStartAdapter = ViewPagerTopImagesAdapter(requireContext(), images)
        mSliderViewPager.adapter = mStartAdapter
        mSliderViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                setUpIndicator(position)

            }

            override fun onPageScrollStateChanged(state: Int) { }

        })
        // Launch coroutine on a background thread
        lifecycleScope.launch(Dispatchers.IO) {
            while (coroutineContext.isActive) {
                // Update interval
                delay(5000)
                // Switch to Main thread
                withContext(Dispatchers.Main) {
                    // Update UI
                    if (mSliderViewPager.currentItem == images.size - 1) {
                        mSliderViewPager.currentItem = 0
                    } else {
                        mSliderViewPager.currentItem = mSliderViewPager.currentItem + 1
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        var weatherView: ImageView = activity?.findViewById(R.id.image_view_weather)!!

        sessionTextView = activity?.findViewById(R.id.text_view_session)!!
        if (hour in 0..11) {
            sessionTextView.text = "Good Morning"
            weatherView.setImageResource(R.drawable.ic_day)
        } else if (hour in 12..15) {
            sessionTextView.text = "Good Afternoon"
            weatherView.setImageResource(R.drawable.ic_day)

        } else if (hour in 16..20) {
            sessionTextView.text = "Good Evening"
            weatherView.setImageResource(R.drawable.ic_night)

        } else if (hour in 21..24) {
            sessionTextView.text = "Good Night"
            weatherView.setImageResource(R.drawable.ic_night)

        }
    }

    private fun getitem(i: Int): Int {
        return mSliderViewPager.getCurrentItem() + i
    }

    fun setUpIndicator(position: Int){
        mDots = Array(images.size){TextView(requireContext())}
        mDotLayout.removeAllViews()
        for (i in mDots.indices){
            mDots[i] = TextView(requireContext())
            mDots[i].text = Html.fromHtml("&#8226")
            mDots[i].textSize = 35f
            mDots[i].setTextColor(resources.getColor(R.color.indicator_inactive_color, activity?.applicationContext?.theme))
            mDotLayout.addView(mDots[i])
        }
        if (mDots.isNotEmpty()){
            mDots[position].setTextColor(resources.getColor(R.color.indicator_active_color, activity?.applicationContext?.theme))
        }
    }







    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}