package com.lyxiiin.flownote.data.repository

import com.lyxiiin.flownote.data.local.dao.TodoDao
import com.lyxiiin.flownote.data.local.entity.Todo
import com.lyxiiin.flownote.util.toMidnightDayAfterTomorrow
import com.lyxiiin.flownote.util.toMidnightToday
import com.lyxiiin.flownote.util.toMidnightTomorrow
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

    override suspend fun insertTodo(todo: Todo): Result<Long> =
        runCatching { todoDao.insertTodo(todo) }

    override suspend fun updateTodo(todo: Todo): Result<Int> =
        runCatching { todoDao.updateTodo(todo) }

    override suspend fun deleteTodo(todo: Todo): Result<Int> =
        runCatching { todoDao.deleteTodo(todo) }

    override suspend fun deleteTodoById(id: Long): Result<Int> =
        runCatching { todoDao.deleteTodoById(id) }

    override fun getTodayTodos(): Flow<List<Todo>> =
        todoDao.getTodayTodos(System.currentTimeMillis().toMidnightTomorrow())

    override fun getTomorrowTodos(): Flow<List<Todo>> {
        val now = System.currentTimeMillis()
        return todoDao.getTomorrowTodos(now.toMidnightTomorrow(),now.toMidnightDayAfterTomorrow())
    }

    override fun getLaterTodos(): Flow<List<Todo>> =
        todoDao.getLaterTodos(System.currentTimeMillis().toMidnightDayAfterTomorrow())
}
