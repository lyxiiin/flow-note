package com.lyxiiin.flownote.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lyxiiin.flownote.databinding.ItemProfileAppearanceBinding

/** 外观模式选项：显示名称 + 存储值（写死在页面里的静态列表项） */
data class AppearanceOption(
    val displayName: String,
    val value: Int
)

class AppearanceAdapter(
    private val onItemClick: (AppearanceOption) -> Unit = {}
) : ListAdapter<AppearanceOption, AppearanceAdapter.AppearanceViewHolder>(DiffCallback) {

    /** 当前选中的外观模式值，用于显示勾选状态 */
    var selectedThemeMode: Int? = null

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<AppearanceOption>() {
            override fun areItemsTheSame(oldItem: AppearanceOption, newItem: AppearanceOption): Boolean =
                oldItem.value == newItem.value

            override fun areContentsTheSame(oldItem: AppearanceOption, newItem: AppearanceOption): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppearanceViewHolder {
        val binding = ItemProfileAppearanceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppearanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppearanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AppearanceViewHolder(private val binding: ItemProfileAppearanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(option: AppearanceOption) {
            binding.tvMode.text = option.displayName
            // 当前选中模式勾选复选框，其余取消勾选
            binding.cbModeDone.isChecked = option.value == selectedThemeMode
            binding.root.setOnClickListener { onItemClick(option) }
        }
    }
}