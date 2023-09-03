package com.example.travelapp.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.travelapp.R
import com.example.travelapp.data.LocationViewModel
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.LocationRepository
import com.example.travelapp.databinding.FragmentHomeBinding
import com.example.travelapp.ui.adapters.LocationAdapter
import com.example.travelapp.ui.util.FirebaseStorageConstants
import com.example.travelapp.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

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
    val viewModel: LocationViewModel = LocationViewModel(LocationRepository(
        FirebaseFirestore.getInstance(),
        FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
    ))

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

        locationAdapter = LocationAdapter()

        var recyclerView = activity?.findViewById<RecyclerView>(R.id.popular_location_recyclerview)


        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = locationAdapter

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
                        viewModel.getAttrractionLocations(item)
                    }
                    locationAdapter.submitList(it.data)
                }
                is UiState.Failure -> {
                    Log.d("TAG", it.error!!)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()


        val db = Firebase.firestore



        // Create a new user with a first and last name
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 1815
//        )

        // Add a new document with a generated ID
//        db.collection("users")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//            }


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