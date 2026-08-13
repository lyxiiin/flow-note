package com.lyxiiin.flownote.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lyxiiin.flownote.databinding.ItemProfileLanguageBinding

/** 语言选项：显示名称 + 存储值（写死在页面里的静态列表项） */
data class LanguageOption(
    val displayName: String,
    val value: String,
    /** 副标题说明，仅"跟随系统"项展示当前系统语言名，其余项为 null 隐藏 */
    val subtitle: String? = null
)

class LanguageAdapter(
    private val onItemClick: (LanguageOption) -> Unit = {}
) : ListAdapter<LanguageOption, LanguageAdapter.LanguageViewHolder>(DiffCallback) {

    /** 当前选中的语言值，用于显示勾选图标 */
    var selectedLanguageValue: String? = null

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LanguageOption>() {
            override fun areItemsTheSame(oldItem: LanguageOption, newItem: LanguageOption): Boolean =
                oldItem.value == newItem.value

            override fun areContentsTheSame(oldItem: LanguageOption, newItem: LanguageOption): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemProfileLanguageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LanguageViewHolder(private val binding: ItemProfileLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(option: LanguageOption) {
            binding.tvLanguageName.text = option.displayName
            // 副标题：仅"跟随系统"项显示（当前系统语言名），其余项隐藏保持行高一致
            binding.tvLanguageSubtitle.text = option.subtitle
            binding.tvLanguageSubtitle.visibility =
                if (option.subtitle != null) View.VISIBLE else View.GONE
            // 当前选中语言显示勾选图标，其余隐藏（INVISIBLE 保持布局不跳动）
            binding.ivCheck.visibility =
                if (option.value == selectedLanguageValue) View.VISIBLE else View.INVISIBLE
            // 最后一项隐藏分隔线，避免列表末尾悬一条线
            binding.divider.visibility =
                if (bindingAdapterPosition == itemCount - 1) View.GONE else View.VISIBLE
            binding.root.setOnClickListener { onItemClick(option) }
        }
    }
}
