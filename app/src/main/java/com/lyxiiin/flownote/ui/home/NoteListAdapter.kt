package com.lyxiiin.flownote.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import com.lyxiiin.flownote.databinding.ItemClassBinding
import com.lyxiiin.flownote.databinding.ItemNoteBinding
import com.lyxiiin.flownote.util.toDateTimeString

class NoteListAdapter(
    private val onNoteClick: (Note) -> Unit = {},
    private val onCategoryClick: (NoteCategory) -> Unit = {},
    private val onCategoryMenuClick: (NoteCategory) -> Unit = {},
    private val onNoteMenuClick: (Note) -> Unit = {}
) : ListAdapter<NoteListItem, RecyclerView.ViewHolder>(DiffCallback)  {

    companion object {
        private const val TYPE_CATEGORY = 0
        private const val TYPE_NOTE = 1

        val DiffCallback = object : DiffUtil.ItemCallback<NoteListItem>() {
            override fun areItemsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean {
                return when {
                    oldItem is NoteListItem.Category && newItem is NoteListItem.Category ->
                        oldItem.categoryWithCount.category.id == newItem.categoryWithCount.category.id
                    oldItem is NoteListItem.UngroupedNote && newItem is NoteListItem.UngroupedNote ->
                        oldItem.note.id == newItem.note.id
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NoteListItem.Category -> TYPE_CATEGORY
            is NoteListItem.UngroupedNote -> TYPE_NOTE
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_CATEGORY -> CategoryViewHolder(
                ItemClassBinding.inflate(inflater, parent, false)
            )
            else -> NoteViewHolder(
                ItemNoteBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is NoteListItem.Category -> (holder as CategoryViewHolder).bind(item.categoryWithCount)
            is NoteListItem.UngroupedNote -> (holder as NoteViewHolder).bind(item.note)
        }
    }

    inner class CategoryViewHolder(private val binding: ItemClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoteCategoryWithCount) {
            val category = item.category
            binding.tvCategoryName.text = category.name
            binding.tvArticleCount.text = binding.root.context.getString(R.string.category_note_subtotal_format, item.noteCount)
            binding.root.setOnClickListener { onCategoryClick(category) }
            binding.btnCategoryMore.setOnClickListener {
                onCategoryMenuClick(category)
            }
        }
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.tvNoteTitle.text = note.title
            binding.tvNoteContent.text = note.content
            binding.tvNoteTime.text = note.createdAt.toDateTimeString()
            binding.root.setOnClickListener { onNoteClick(note) }
            binding.btnNoteMore.setOnClickListener {
                onNoteMenuClick(note)
            }
        }
    }
}