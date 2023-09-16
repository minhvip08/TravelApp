package com.example.travelapp.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager

import com.example.travelapp.R
import com.example.travelapp.data.ImageViewModel
import com.example.travelapp.data.LocationViewModel
import com.example.travelapp.data.UserViewModel
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.ImageRepository
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.data.repository.UserRepository
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
    lateinit var sessionTextView: TextView
    lateinit var userTextView: TextView
    lateinit var searchLocation: SearchView
    val viewModel: LocationViewModel = LocationViewModel(LocationRepository(
        FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
    ))

    // Viewpager Top Images initializes
    private lateinit var mSliderViewPager: ViewPager
    private lateinit var mDotLayout: LinearLayout
    private lateinit var mDots: Array<TextView>
    private lateinit var mStartAdapter: ViewPagerTopImagesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var avatar: ImageView
    private lateinit var weatherView: ImageView
    private val imageViewModel = ImageViewModel(ImageRepository( FirebaseStorage.getInstance().reference))
    private var locationList = ArrayList<LocationItem>()
    val storage =   FirebaseStorage.getInstance().reference

    private var images: ArrayList<Bitmap> = ArrayList()

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

        userTextView = view.findViewById(R.id.text_view_user)
        userTextView.text = Firebase.auth.currentUser?.displayName ?: getString(R.string.guest)

        locationAdapter = LocationAdapter()

        searchLocation = view.findViewById(R.id.search_location)

        recyclerView = view.findViewById(R.id.popular_location_recyclerview)!!

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = locationAdapter

        recyclerView = view.findViewById(R.id.popular_location_recyclerview)!!
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
                    locationList = it.data as ArrayList<LocationItem>
                    locationList.forEach { location ->
                        updateImage(location.image) { imagePath ->
                            location.imagePath = imagePath
                            locationAdapter.notifyItemChanged(locationList.indexOf(location))
                        }
                    }
                    createTopImage()
                }
                is UiState.Failure -> {
                    Log.d("TAG", it.error!!)
                }

                else -> {
                    Log.d("TAG", "Else")}
            }
        }
        avatar = view.findViewById(R.id.home_avatar)
        // Set avatar
        getAvatar()

        userTextView.text = Firebase.auth.currentUser?.displayName ?: getString(R.string.guest)
        mDotLayout = view.findViewById(R.id.dots_layout_top_images)!!
        mSliderViewPager = view.findViewById(R.id.top_image_viewpager)!!
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

        locationAdapter.submitList(locationList)
        weatherView = view.findViewById(R.id.image_view_weather)!!

        sessionTextView = view.findViewById(R.id.text_view_session)!!

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

    private val handleAvatarUpdate = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val result = it.data
            if (result != null) {
                val avatarChanged = result.getBooleanExtra("avatarChanged", false)
                if (avatarChanged) {
                    getAvatar()
                }
                val nameChanged = result.getBooleanExtra("nameChanged", false)
                if (nameChanged) {
                    userTextView.text = Firebase.auth.currentUser?.displayName ?: getString(R.string.guest)
                }
            }
        }
    }

    private fun getAvatar() {
        userViewModel.getAvatar(Firebase.auth.currentUser!!.uid) { path ->
            if (path.isNotEmpty()) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    withContext(Dispatchers.Main) {
                        avatar.setImageBitmap(bitmap)
                    }
                }
            }
            avatar.setOnClickListener {
                val intent = Intent(requireActivity(), UserProfileActivity::class.java)
                handleAvatarUpdate.launch(intent, null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val c = Calendar.getInstance()
        when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> {
                sessionTextView.text = getString(R.string.good_morning)
                weatherView.setImageResource(R.drawable.ic_day)
            }
            in 12..15 -> {
                sessionTextView.text = getString(R.string.good_afternoon)
                weatherView.setImageResource(R.drawable.ic_day)

            }
            in 16..20 -> {
                sessionTextView.text = getString(R.string.good_evening)
                weatherView.setImageResource(R.drawable.ic_night)

            }
            in 21..24 -> {
                sessionTextView.text = getString(R.string.good_night)
                weatherView.setImageResource(R.drawable.ic_night)
            }
        }
    }

    private fun createTopImage(){
        locationList.forEach { location ->
            imageViewModel.getImage("top_image_${location.image}_1"){
                updateUI(it)
            }
            imageViewModel.getImage("top_image_${location.image}_2"){
                updateUI(it)
            }
        }

    }

    private fun updateImage(imageId: String, updateUi: (String) -> Unit) {
        imageViewModel.getImagePath(imageId, updateUi)
    }

    private fun updateUI(bitmap: Bitmap){
        images.add(bitmap)
        mStartAdapter.notifyDataSetChanged()
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

        private val userViewModel = UserViewModel(
            UserRepository(
                FirebaseFirestore.getInstance(),
                FirebaseStorage.getInstance().reference
            )
        )
    }
}