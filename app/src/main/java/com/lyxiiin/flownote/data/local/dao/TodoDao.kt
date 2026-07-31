package com.lyxiiin.flownote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lyxiiin.flownote.data.local.entity.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo): Int

    @Delete
    suspend fun deleteTodo(todo: Todo): Int

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun deleteTodoById(id: Long): Int

    @Query("SELECT * FROM todos ORDER BY created_at DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id")
    fun getTodoById(id: Long): Flow<Todo?>

    @Query("SELECT * FROM todos WHERE is_done = :state ORDER BY created_at DESC")
    fun getTodosByState(state: Boolean): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE is_done = 0 AND due_date IS NOT NULL ORDER BY due_date ASC")
    fun getUpcomingTodos(): Flow<List<Todo>>
}