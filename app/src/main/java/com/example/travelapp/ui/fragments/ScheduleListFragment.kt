package com.example.travelapp.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.ImageViewModel
import com.example.travelapp.data.models.ScheduleItem
import com.example.travelapp.data.repository.ImageRepository
import com.example.travelapp.ui.adapters.ScheduleAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

private const val ARG_IS_HISTORY = "isHistory"
private const val ARG_SCHEDULE_LIST = "scheduleList"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleListFragment : Fragment() {
    private var isHistory = false
    private var scheduleList: ArrayList<ScheduleItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isHistory = it.getBoolean(ARG_IS_HISTORY)
            scheduleList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelableArrayList(ARG_SCHEDULE_LIST, ScheduleItem::class.java)
            } else {
                it.getParcelableArrayList(ARG_SCHEDULE_LIST)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scheduleRecyclerView = view.findViewById<RecyclerView>(R.id.schedule_list_item)
        val scheduleAdapter = ScheduleAdapter(requireParentFragment().parentFragmentManager, isHistory)
        scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        scheduleRecyclerView.adapter = scheduleAdapter
        scheduleAdapter.submitList(scheduleList)
        if (!isHistory) {
            val swipeToCancelText = view.findViewById<TextView>(R.id.swipe_to_cancel)
            swipeToCancelText.visibility = TextView.VISIBLE
            val itemTouchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    (parentFragment as ScheduleFragment).viewModel.delete(Firebase.auth.currentUser!!.uid, (viewHolder as ScheduleAdapter.ViewHolder).id) {
                        scheduleList?.removeAt(position)
                        scheduleAdapter.notifyItemRemoved(position)
                    }
                }
            })
            itemTouchHelper.attachToRecyclerView(scheduleRecyclerView)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(isHistory: Boolean, list: ArrayList<ScheduleItem>) =
            ScheduleListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_HISTORY, isHistory)
                    putParcelableArrayList(ARG_SCHEDULE_LIST, list)
                }
            }
    }
}