package com.example.travelapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import com.example.travelapp.R
import com.example.travelapp.data.LocationViewModel
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.ui.util.FirebaseStorageConstants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class LocationDetailActivity : AppCompatActivity() {
    lateinit var enterPlanbtn: TextView
    lateinit var attractionName: TextView
    lateinit var addToPlanBtn: TextView
    private lateinit var mActionBarToolbar: Toolbar
    private lateinit var ratingBtn: Button
    private lateinit var titleToolBar: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var bannerImg: ImageView
    private var isRated: Boolean? = null
    private var rating: Long = 0

    private val viewModel = LocationViewModel(
        LocationRepository(
            FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_detail)

        // Get the location item from the intent
        // Because the location item is a custom object, we need to use the getParcelableExtra method
        // to get the location item from the intent
        // getParcelableExtra is deprecated in API 30, so we need to use the if statement to check
        // the API version
        val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", LocationItem::class.java)
        } else {
            intent.getParcelableExtra<LocationItem>("location")
        }

        addToPlanBtn = findViewById(R.id.add_to_plan_button)
        addToPlanBtn.setOnClickListener(){
            var intent: Intent = Intent(this, SetPeriodActivity::class.java)
            intent.putExtra("location", item)
            startActivity(intent)
        }

        descriptionTextView = findViewById(R.id.attraction_description)
        descriptionTextView.setText(item?.description)
        bannerImg = findViewById(R.id.image_view_detail_activity)

        var idImage = this.resources.getIdentifier(item?.image,
            "drawable", this.packageName) // R.drawable.image_name


        bannerImg.setImageResource(idImage)
        // Set up action bar
        enterPlanbtn = findViewById(R.id.enter_plan_button)
        attractionName = findViewById(R.id.attraction_name)
        attractionName.setText(item?.title)
        enterPlanbtn.setOnClickListener(){
            var intent: Intent = Intent(this, MapActivity::class.java)
            intent.putExtra("location", item)
            startActivity(intent)
        }
        setupToolBar()
        // Get user id
        val uid = Firebase.auth.currentUser!!.uid
        // Rating button
        ratingBtn = findViewById(R.id.rating_button)
        // Check if user has rated this location
        viewModel.checkIfUserRatingExist(uid, item!!) {
            if (it) {
                isRated = true
                DrawableCompat.setTint(
                    ratingBtn.compoundDrawables[0],
                    getColor(R.color.blue)
                )
            }
            else {
                isRated = false
                DrawableCompat.setTint(
                    ratingBtn.compoundDrawables[0],
                    getColor(R.color.white)
                )
            }
        }
        viewModel.getLocationRating(item) {
            rating = it - 1
            ratingBtn.text = rating.toString()
        }
        ratingBtn.setOnClickListener {
            if (isRated != null) {
                if (isRated as Boolean) {
                    viewModel.removeUserRating(uid, item) {
                        isRated = false
                        --rating
                        ratingBtn.text = rating.toString()
                        DrawableCompat.setTint(
                            ratingBtn.compoundDrawables[0],
                            getColor(R.color.white)
                        )
                    }
                }
                else {
                    viewModel.addUserRating(uid, item) {
                        isRated = true
                        ++rating
                        ratingBtn.text = rating.toString()
                        DrawableCompat.setTint(
                            ratingBtn.compoundDrawables[0],
                            getColor(R.color.blue)
                        )
                    }
                }
            }
        }
    }

    private fun setupToolBar(){
        mActionBarToolbar = findViewById(R.id.toolbar_layout_detail_activity)
        setSupportActionBar(mActionBarToolbar)
        supportActionBar?.show()
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}