package com.example.travelapp.ui

import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.HotelViewModel
import com.example.travelapp.data.ImageViewModel
import com.example.travelapp.data.models.HotelItem
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.data.repository.HotelRepository
import com.example.travelapp.data.repository.ImageRepository
import com.example.travelapp.ui.adapters.HotelAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ChooseHotelActivity : AppCompatActivity() {
    private lateinit var mActionBarToolbar: Toolbar
    private lateinit var titleToolBar: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var hotelAdapter: HotelAdapter
    var hotelList = ArrayList<HotelItem>()
    val viewModel = HotelViewModel(HotelRepository(FirebaseFirestore.getInstance()))
    val storage =   FirebaseStorage.getInstance().reference
    var ImageViewModel = ImageViewModel(ImageRepository( storage))
    private var scheduleItem: ScheduleItem?= null
    private var locationItem: LocationItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_hotel)

        setupToolBar()
        getItemFromIntent()

        hotelAdapter = HotelAdapter(hotelList, locationItem!!, scheduleItem!!)

        recyclerView = findViewById(R.id.hotel_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = hotelAdapter

        viewModel.getHotels(locationItem!!.id) { list ->
            hotelList.clear()
            hotelList.addAll(list)
            hotelAdapter.notifyItemRangeInserted(0, list.size)
            hotelList.forEach { hotel ->
                updateImage(hotel.imageId) { imagePath ->
                    hotel.imagePath = imagePath
                    hotelAdapter.notifyItemChanged(hotelList.indexOf(hotel))
                }
            }
        }
    }

    fun updateImage(imageId: String, updateUi: (String) -> Unit) {
        ImageViewModel.getImagePath(imageId, updateUi)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupToolBar(){
        mActionBarToolbar = findViewById(R.id.toolbar_layout)
        setSupportActionBar(mActionBarToolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleToolBar = findViewById(R.id.toolbar_title)
        titleToolBar.text = "Choose Hotel"

    }

    private fun getItemFromIntent(){
        locationItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", LocationItem::class.java)
        } else {
            intent.getParcelableExtra("location")
        }

        scheduleItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("schedule", ScheduleItem::class.java)
        } else {
            intent.getParcelableExtra("schedule")
        }
    }
}