package com.example.travelapp.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.data.models.ArticleItem
import com.example.travelapp.ui.ReadArticleActivity

class ArticleAdapter
    : ListAdapter<ArticleItem, ArticleAdapter.ViewHolder>(ArticleItemDiff()) {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val author: TextView = itemView.findViewById(R.id.article_item_author)
        val title: TextView = itemView.findViewById(R.id.article_item_title)
        val thumbnail: ImageView = itemView.findViewById(R.id.article_item_thumbnail)
        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.article_item, parent, false)
                return ViewHolder(view)
            }
        }
    }

    class ArticleItemDiff : DiffUtil.ItemCallback<ArticleItem>() {
        override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
            return oldItem.pid == newItem.pid
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.title.text = currentItem.title
        holder.author.text = currentItem.author
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ReadArticleActivity::class.java)
            intent.putExtra("url", currentItem.url)
            holder.itemView.context.startActivity(intent)
        }
    }
}