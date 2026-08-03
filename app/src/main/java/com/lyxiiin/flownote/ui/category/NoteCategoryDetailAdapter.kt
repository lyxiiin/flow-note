package com.lyxiiin.flownote.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategory

import com.lyxiiin.flownote.databinding.ItemNoteBinding
import com.lyxiiin.flownote.util.toDateTimeString

class NoteCategoryDetailAdapter(
    private val onNoteClick: (Note) -> Unit = {},
    private val onNoteMenuClick: (Note) -> Unit = {},
)  :
    ListAdapter<Note, NoteCategoryDetailAdapter.ViewHolder>(DiffCallback)  {

    inner class ViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(item: Note) {
                binding.tvNoteTitle.text = item.title
                binding.tvNoteContent.text = item.content
                binding.tvNoteTime.text = item.updatedAt.toDateTimeString()
                binding.root.setOnClickListener { onNoteClick(item) }
                binding.btnNoteMore.setOnClickListener {
                    onNoteMenuClick(item)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Note>(){
            override fun areItemsTheSame(p0: Note, p1: Note): Boolean {
                return p0.id == p1.id
            }

            override fun areContentsTheSame(p0: Note, p1: Note): Boolean {
                return p0 == p1
            }
        }
    }
}