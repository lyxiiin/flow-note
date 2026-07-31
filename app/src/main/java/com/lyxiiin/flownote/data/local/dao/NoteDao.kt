package com.lyxiiin.flownote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lyxiiin.flownote.data.local.entity.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note): Int

    @Delete
    suspend fun deleteNote(note: Note): Int

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long): Int

    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Long): Flow<Note?>

    @Query("SELECT * FROM notes WHERE category_id = :categoryId ORDER BY created_at DESC")
    fun getNotesByCategory(categoryId: Long): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :keyword || '%' OR content LIKE '%' || :keyword || '%' ORDER BY created_at DESC")
    fun searchNotes(keyword: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE category_id IS NULL ORDER BY created_at DESC")
    fun getUngroupedNotes(): Flow<List<Note>>
}