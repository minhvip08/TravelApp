package com.example.travelapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.travelapp.R
import com.example.travelapp.data.LocationViewModel
import com.example.travelapp.data.models.Attraction
import com.example.travelapp.data.models.LocationItem
import com.example.travelapp.data.repository.LocationRepositoryImp
import com.example.travelapp.databinding.FragmentHomeBinding
import com.example.travelapp.ui.adapters.LocationAdapter
import com.example.travelapp.ui.util.UiState
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var locationAdapter: LocationAdapter
    lateinit var locationRecyclerView: RecyclerView
    val viewModel: LocationViewModel by viewModels()
    val adapter by lazy {
        LocationAdapter()

    }



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var locationList: List<LocationItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onStart() {
        super.onStart()




        binding.popularLocationRecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.popularLocationRecyclerview.setHasFixedSize(true)
        binding.popularLocationRecyclerview.adapter = adapter



    }



    //Test app without Firebase
    private fun getLocationData(){
        // Firebase
//        dbref = FirebaseDatabase.getInstance().getReference("Users")
//        dbref.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.exists()){
//                    for (userSnapshot in snapshot.children){
//                        val user = userSnapshot.getValue(User::class.java)
//                        userArrayList.add(user!!)
//                    }
//                    userRecyclerview.adapter = MyAdapter(userArrayList)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
        var listAttraction: List<Attraction> = arrayListOf<Attraction>()


        var listLocation = arrayListOf<LocationItem>()
        val locationItem1 = LocationItem("1","Switzerland" ,R.drawable.switzerland);
        listLocation.add(locationItem1)
        var locationAdapter = LocationAdapter()
        locationRecyclerView.adapter = locationAdapter




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