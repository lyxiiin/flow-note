package com.lyxiiin.flownote.ui.home

import androidx.annotation.StringRes
import com.lyxiiin.flownote.data.local.entity.Todo

sealed class TodoListItem {
    data class Header(@StringRes val titleRes: Int, val count: Int = 0) : TodoListItem()
    data class TodoRow(val todo: Todo) : TodoListItem()
}