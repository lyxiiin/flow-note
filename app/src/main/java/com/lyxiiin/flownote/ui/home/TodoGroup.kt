package com.lyxiiin.flownote.ui.home

import androidx.annotation.StringRes
import com.lyxiiin.flownote.data.local.entity.Todo

/** @param titleRes 分组标题的字符串资源 ID，由 UI 层解析为显示文本 */
data class TodoGroup (
    @StringRes val titleRes: Int,
    val todos: List<Todo>
)