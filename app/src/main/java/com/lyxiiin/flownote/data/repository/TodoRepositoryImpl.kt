package com.lyxiiin.flownote.data.repository

import com.lyxiiin.flownote.data.local.dao.TodoDao
import com.lyxiiin.flownote.data.local.entity.Todo
import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl(
    private val todoDao: TodoDao
) : TodoRepository {

    override fun getAllTodos(): Flow<List<Todo>> =
        todoDao.getAllTodos()

    override fun getTodoById(id: Long): Flow<Todo?> =
        todoDao.getTodoById(id)

    override fun getTodosByState(state: Boolean): Flow<List<Todo>> =
        todoDao.getTodosByState(state)

    override fun getUpcomingTodos(): Flow<List<Todo>> =
        todoDao.getUpcomingTodos()

    override suspend fun insertTodo(todo: Todo): Result<Long> =
        runCatching { todoDao.insertTodo(todo) }

    override suspend fun updateTodo(todo: Todo): Result<Int> =
        runCatching { todoDao.updateTodo(todo) }

    override suspend fun deleteTodo(todo: Todo): Result<Int> =
        runCatching { todoDao.deleteTodo(todo) }

    override suspend fun deleteTodoById(id: Long): Result<Int> =
        runCatching { todoDao.deleteTodoById(id) }
}
