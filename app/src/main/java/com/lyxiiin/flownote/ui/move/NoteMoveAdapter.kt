package com.lyxiiin.flownote.ui.move

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import com.lyxiiin.flownote.databinding.ItemMoveCategoryBinding

class NoteMoveAdapter(
    private val onItemClick: (NoteCategory) -> Unit = {}
) : ListAdapter<NoteCategoryWithCount, NoteMoveAdapter.NoteMoveViewHolder>(DiffCallback) {

    /** 当前笔记所在分组 ID，用于在对应分组上显示勾选图标 */
    var selectedCategoryId: Long? = null

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<NoteCategoryWithCount>() {
            override fun areItemsTheSame(oldItem: NoteCategoryWithCount, newItem: NoteCategoryWithCount): Boolean =
                oldItem.category.id == newItem.category.id

            override fun areContentsTheSame(oldItem: NoteCategoryWithCount, newItem: NoteCategoryWithCount): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteMoveViewHolder {
        val binding = ItemMoveCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NoteMoveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteMoveViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteMoveViewHolder(private val binding: ItemMoveCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: NoteCategoryWithCount) {
            binding.tvTitle.text = category.category.name
            binding.tvNoteCount.text = category.noteCount.toString()
            // 当前笔记所在分组显示勾选图标，其余分组隐藏（INVISIBLE 保持布局不跳动）
            binding.ivCheck.visibility =
                if (category.category.id == selectedCategoryId) View.VISIBLE else View.INVISIBLE
            binding.root.setOnClickListener { onItemClick(category.category) }
        }
    }
}
