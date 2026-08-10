package com.lyxiiin.flownote.ui.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lyxiiin.flownote.data.repository.TodoRepository

class TodoEditViewModelFactory(
    private val todoId: Long,
    private val todoRepository: TodoRepository,
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TodoEditViewModel::class.java)){
            return TodoEditViewModel(todoId, todoRepository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class : ${modelClass.name}")
    }
}