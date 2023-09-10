package com.example.travelapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.LocationViewModel
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.databinding.ActivitySearchLocationBinding
import com.example.travelapp.ui.adapters.LocationAdapter
import com.example.travelapp.ui.util.FirebaseStorageConstants
import com.example.travelapp.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SearchLocationActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchLocationBinding
    private var locationList = ArrayList<LocationItem>()
    lateinit var locationAdapter: LocationAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var searchView: SearchView
    val viewModel: LocationViewModel = LocationViewModel(
        LocationRepository(
            FirebaseFirestore.getInstance(),
            FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
        )
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_location)






        locationAdapter = LocationAdapter()

        recyclerView = findViewById(R.id.recycler_view_search)

        recyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(
                this,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = locationAdapter

        FirebaseFirestore.setLoggingEnabled(true)
        viewModel.getLocations().observe(this) {
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
                }

                is UiState.Failure -> {
                    Log.d("TAG", it.error!!)
                }

                else -> {
                    Log.d("TAG", "Else")
                }
            }
        }

        searchView = findViewById(R.id.search_box)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                filterList(query!!)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText!!)
                return false
            }
        })


    }

    private fun filterList(searchText: String) {
        val filteredList = ArrayList<LocationItem>()
        for (item in locationList) {
            if (item.title.toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(item)
            }
        }
        locationAdapter.submitList(filteredList)
    }


}