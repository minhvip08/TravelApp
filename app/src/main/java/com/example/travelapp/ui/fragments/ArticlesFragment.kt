package com.example.travelapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.ArticleViewModel
import com.example.travelapp.data.repository.ArticleRepository
import com.example.travelapp.ui.adapters.ArticleAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ArticlesFragment : Fragment() {

    private val articleViewModel = ArticleViewModel(
        ArticleRepository (
            FirebaseFirestore.getInstance()
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val articleAdapter = ArticleAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.articles_recycler_view)
        recyclerView.adapter = articleAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        articleViewModel.getArticles {
            articleAdapter.submitList(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArticlesFragment()
    }
}