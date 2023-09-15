package com.example.travelapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.travelapp.R
import com.example.travelapp.data.ArticleViewModel
import com.example.travelapp.data.models.ArticleItem
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

    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var articleList = mutableListOf<ArticleItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        articleAdapter = ArticleAdapter()
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView = view.findViewById(R.id.articles_recycler_view)
        recyclerView.layoutManager = linearLayoutManager
        loadArticles()
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.articles_refresh_layout)
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            articleViewModel.clearArticlesQueue()
            articleAdapter.notifyItemRangeRemoved(0, articleList.size)
            articleList.clear()
            recyclerView.clearOnScrollListeners()
            loadArticles()
        }
    }

    private fun loadArticles() {
        articleViewModel.getArticles { initList ->
            recyclerView.adapter = articleAdapter
            articleList.addAll(initList)
            articleAdapter.submitList(articleList)
            recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if ((linearLayoutManager.findLastCompletelyVisibleItemPosition() == articleAdapter.itemCount - 1)) {
                        articleViewModel.getArticles { loadMoreList ->
                            if (loadMoreList.isEmpty()) {
                                // Avoid crashing when there is no more article to load
                                recyclerView.removeOnScrollListener(this)
                            }
                            else {
                                articleList.addAll(loadMoreList)
                                articleAdapter.submitList(articleList)
                            }
                        }
                    }
                }
            })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArticlesFragment()
    }
}