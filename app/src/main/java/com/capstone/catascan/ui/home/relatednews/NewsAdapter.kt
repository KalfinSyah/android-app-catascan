package com.capstone.catascan.ui.home.relatednews

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.catascan.data.api.ArticlesItem
import com.capstone.catascan.databinding.ItemNewsBinding

class NewsAdapter: ListAdapter<ArticlesItem, NewsAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        Glide.with(holder.itemView.context)
            .load(news.urlToImage)
            .into(holder.binding.newsImage)
        holder.binding.newsTitle.text = news.title

        holder.itemView.setOnClickListener {
            if (!news.url.isNullOrEmpty()) {
                val context = holder.itemView.context
                val intent = Intent(context, NewsDetailActivity::class.java).apply {
                    putExtra("URL_EXTRA", news.url)
                }
                context.startActivity(intent)
            }
        }

    }

    class MyViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}