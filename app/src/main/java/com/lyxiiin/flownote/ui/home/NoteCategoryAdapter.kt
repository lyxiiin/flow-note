package com.lyxiiin.flownote.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import com.lyxiiin.flownote.databinding.ItemClassBinding

class NoteCategoryAdapter :
    ListAdapter<NoteCategoryWithCount, NoteCategoryAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoteCategoryWithCount) {
            binding.tvCategoryName.text = item.category.name
            binding.tvArticleCount.text = "${item.noteCount}篇小计"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemClassBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<NoteCategoryWithCount>() {
            override fun areItemsTheSame(oldItem: NoteCategoryWithCount, newItem: NoteCategoryWithCount): Boolean {
                return oldItem.category.id == newItem.category.id
            }

            override fun areContentsTheSame(oldItem: NoteCategoryWithCount, newItem: NoteCategoryWithCount): Boolean {
                return oldItem == newItem
            }
        }
    }
}
