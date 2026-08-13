package com.lyxiiin.flownote.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.data.local.entity.Todo
import com.lyxiiin.flownote.databinding.ItemTodoBinding
import com.lyxiiin.flownote.databinding.ItemTodoGroupHeaderBinding
import com.lyxiiin.flownote.util.toMidnightToday
import com.lyxiiin.flownote.util.toSmartDateString

class TodoListAdapter(
    private val onTodoClick: (Todo) -> Unit = {},
    private val onCheckBoxClick: (Todo) -> Unit = {},
    private val onTodoMenuClick: (Todo) -> Unit = {}
) : ListAdapter<TodoListItem, RecyclerView.ViewHolder>(DiffCallback) {
    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_TODO = 1
        val DiffCallback = object : DiffUtil.ItemCallback<TodoListItem>() {
            override fun areItemsTheSame(p0: TodoListItem, p1: TodoListItem): Boolean {
                return when {
                    p0 is TodoListItem.Header && p1 is TodoListItem.Header ->
                        p0.titleRes == p1.titleRes
                    p0 is TodoListItem.TodoRow && p1 is TodoListItem.TodoRow ->
                        p0.todo.id == p1.todo.id
                    else -> false
                }
            }
            override fun areContentsTheSame(p0: TodoListItem, p1: TodoListItem): Boolean {
                return p0 == p1
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is TodoListItem.Header -> TYPE_HEADER
            is TodoListItem.TodoRow -> TYPE_TODO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            TYPE_TODO -> TodoViewHolder(ItemTodoBinding.inflate(inflater,parent,false))
            else -> HeaderViewHolder(ItemTodoGroupHeaderBinding.inflate(inflater,parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)){
            is TodoListItem.Header -> (holder as HeaderViewHolder).bind(item.titleRes, item.count)
            is TodoListItem.TodoRow -> (holder as TodoViewHolder).bind(item.todo)
        }
    }

    inner class TodoViewHolder(private val binding: ItemTodoBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(todo: Todo){
            binding.tvTodoTitle.text = todo.title
            binding.cbTodoDone.isChecked = todo.isDone
            binding.cbTodoDone.setOnClickListener { onCheckBoxClick(todo) }
            binding.root.setOnClickListener { onTodoClick(todo) }
            binding.btnTodoMore.setOnClickListener { onTodoMenuClick(todo) }

            // 截止日期：无截止日期隐藏该行；过期时文字与图标置红
            val dueDate = todo.dueDate
            if (dueDate == null) {
                binding.llTodoDue.visibility = View.GONE
            } else {
                binding.llTodoDue.visibility = View.VISIBLE
                binding.tvTodoDue.text = dueDate.toSmartDateString()
                val isOverdue = dueDate < System.currentTimeMillis().toMidnightToday()
                val context = binding.root.context
                if (isOverdue) {
                    binding.tvTodoDue.setTextColor(ContextCompat.getColor(context, R.color.error))
                    binding.ivTodoDueIcon.setColorFilter(ContextCompat.getColor(context, R.color.error))
                } else {
                    binding.tvTodoDue.setTextColor(ContextCompat.getColor(context, R.color.text_hint))
                    binding.ivTodoDueIcon.clearColorFilter()
                }
            }
        }
    }
    inner class HeaderViewHolder(private val binding: ItemTodoGroupHeaderBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(@StringRes titleRes: Int, count: Int){
            binding.tvTitle.text = binding.root.context.getString(titleRes)
            binding.tvCount.text = binding.root.context.getString(R.string.todo_count_format, count)
        }
    }

}