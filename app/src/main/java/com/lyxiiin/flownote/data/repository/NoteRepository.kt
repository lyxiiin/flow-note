package com.lyxiiin.flownote.data.repository

import com.lyxiiin.flownote.data.local.entity.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNote(): Flow<List<Note>>

    fun getNoteById(id: Long): Flow<Note?>

    fun getNoteByCategory(categoryId: Long): Flow<List<Note>>

    fun searchNotes(keyword: String): Flow<List<Note>>

    fun getUngroupedNotes(): Flow<List<Note>>

    suspend fun insertNote(note: Note): Result<Long>

    suspend fun updateNote(note: Note): Result<Int>

    suspend fun deleteNote(note: Note): Result<Int>

    suspend fun deleteNoteById(id: Long): Result<Int>
}