package com.lyxiiin.flownote.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.databinding.DialogActionMenuBinding
import com.lyxiiin.flownote.databinding.ItemActionMenuBinding

/**
 * 现代化底部操作菜单组件（Bottom Sheet 风格）。
 *
 * 特性：
 * - 顶部圆角 + 拖拽指示条
 * - 菜单项支持图标 + 文字，带圆角水波纹反馈
 * - 危险操作（如删除）自动标红
 * - Builder 模式，灵活组合菜单项
 *
 * 使用示例：
 * ```
 * ActionMenuDialog.Builder(context)
 *     .setTitle("工作笔记")
 *     .addItem(R.drawable.ic_edit, "重命名") { /* 重命名逻辑 */ }
 *     .addDangerItem(R.drawable.ic_delete, "删除") { /* 删除逻辑 */ }
 *     .show()
 * ```
 */
class ActionMenuDialog private constructor(
    private val dialog: BottomSheetDialog
) {

    fun show() = dialog.show()

    fun dismiss() = dialog.dismiss()

    /** 菜单项数据 */
    private data class MenuItem(
        @DrawableRes val iconRes: Int,
        val label: String,
        val isDanger: Boolean = false,
        val onClick: () -> Unit
    )

    class Builder(private val context: Context) {
        private var title: String = ""
        private val items = mutableListOf<MenuItem>()

        /** 设置菜单标题（通常为操作对象的名称） */
        fun setTitle(title: String) = apply { this.title = title }

        /** 添加普通菜单项 */
        fun addItem(@DrawableRes iconRes: Int, label: String, onClick: () -> Unit) = apply {
            items.add(MenuItem(iconRes, label, isDanger = false, onClick = onClick))
        }

        /** 添加危险操作菜单项（文字标红，用于删除等不可逆操作） */
        fun addDangerItem(@DrawableRes iconRes: Int, label: String, onClick: () -> Unit) = apply {
            items.add(MenuItem(iconRes, label, isDanger = true, onClick = onClick))
        }

        fun build(): ActionMenuDialog {
            val binding = DialogActionMenuBinding.inflate(LayoutInflater.from(context))
            val dialog = BottomSheetDialog(context)
            dialog.setContentView(binding.root)

            // 移除 BottomSheet 默认背景，让自定义圆角背景生效
            dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.setBackgroundResource(android.R.color.transparent)

            // 标题：为空时隐藏
            binding.tvMenuTitle.text = title
            binding.tvMenuTitle.visibility = if (title.isEmpty()) View.GONE else View.VISIBLE

            // 动态填充菜单项
            items.forEach { item ->
                val itemBinding = ItemActionMenuBinding.inflate(
                    LayoutInflater.from(context), binding.llMenuItems, false
                )
                itemBinding.ivMenuItemIcon.setImageResource(item.iconRes)
                itemBinding.tvMenuItemLabel.text = item.label

                // 危险操作文字标红
                if (item.isDanger) {
                    itemBinding.tvMenuItemLabel.setTextColor(
                        ContextCompat.getColor(context, R.color.error)
                    )
                }

                itemBinding.root.setOnClickListener {
                    dialog.dismiss()
                    item.onClick()
                }
                binding.llMenuItems.addView(itemBinding.root)
            }

            return ActionMenuDialog(dialog)
        }

        /** 构建并立即显示 */
        fun show(): ActionMenuDialog = build().also { it.show() }
    }
}
