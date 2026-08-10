package com.lyxiiin.flownote.ui.home

import com.lyxiiin.flownote.data.local.entity.Todo

data class TodoGroup (
    val title: String,
    val todos: List<Todo>
)