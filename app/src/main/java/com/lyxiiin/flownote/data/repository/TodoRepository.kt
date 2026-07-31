package com.lyxiiin.flownote.data.repository

import com.lyxiiin.flownote.data.local.entity.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<Todo>>

    fun getTodoById(id: Long): Flow<Todo?>

    fun getTodosByState(state: Boolean): Flow<List<Todo>>

    fun getUpcomingTodos(): Flow<List<Todo>>

    suspend fun insertTodo(todo: Todo): Result<Long>

    suspend fun updateTodo(todo: Todo): Result<Int>

    suspend fun deleteTodo(todo: Todo): Result<Int>

    suspend fun deleteTodoById(id: Long): Result<Int>
}
