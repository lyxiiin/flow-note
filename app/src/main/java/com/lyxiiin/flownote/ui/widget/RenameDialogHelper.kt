package com.lyxiiin.flownote.ui.widget

import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

/**
 * 通用重命名弹窗。
 *
 * 适用于分组、笔记等任意列表项的重命名场景：
 * - 预填当前名称并全选，方便直接修改
 * - 自动校验：空名称或未修改时不触发回调
 * - 成功后统一 Toast 提示
 *
 * 使用示例：
 * ```
 * showRenameDialog(
 *     dialogTitle = "重命名分组",
 *     hint = "请输入新的分组名称",
 *     currentName = category.name
 * ) { newName ->
 *     viewModel.renameCategory(category.copy(name = newName, updatedAt = System.currentTimeMillis()))
 * }
 * ```
 *
 * @param dialogTitle 弹窗标题，如"重命名分组"、"重命名笔记"
 * @param hint 输入框提示文案
 * @param currentName 当前名称，用于预填
 * @param onConfirm 校验通过后的回调，参数为去除首尾空白的新名称
 */
fun Fragment.showRenameDialog(
    dialogTitle: String,
    hint: String,
    currentName: String,
    onConfirm: (String) -> Unit
) {
    val editText = EditText(requireContext()).apply {
        this.hint = hint
        setText(currentName)
        setPadding(64, 32, 64, 32)
        post { selectAll() }
    }

    AlertDialog.Builder(requireContext())
        .setTitle(dialogTitle)
        .setView(editText)
        .setPositiveButton("确定") { _, _ ->
            val newName = editText.text.toString().trim()
            if (newName.isNotEmpty() && newName != currentName) {
                onConfirm(newName)
                Toast.makeText(requireContext(), "重命名成功", Toast.LENGTH_SHORT).show()
            }
        }
        .setNegativeButton("取消", null)
        .show()
}
