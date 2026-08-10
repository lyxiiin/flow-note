package com.lyxiiin.flownote.ui.home

import com.lyxiiin.flownote.data.local.entity.Todo

sealed class TodoListItem {
    data class Header(val title: String, val count: Int = 0) : TodoListItem()
    data class TodoRow(val todo: Todo) : TodoListItem()
}