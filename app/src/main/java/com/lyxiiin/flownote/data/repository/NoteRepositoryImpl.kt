package com.lyxiiin.flownote.data.repository

import com.lyxiiin.flownote.data.local.dao.NoteDao
import com.lyxiiin.flownote.data.local.entity.Note
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNote(): Flow<List<Note>> =
        noteDao.getAllNotes()

    override fun getNoteById(id: Long): Flow<Note?> =
        noteDao.getNoteById(id)

    override fun getNoteByCategory(categoryId: Long): Flow<List<Note>> =
        noteDao.getNotesByCategory(categoryId)

    override fun searchNotes(keyword: String): Flow<List<Note>> =
        noteDao.searchNotes(keyword)

    override fun getUngroupedNotes(): Flow<List<Note>> = noteDao.getUngroupedNotes()

    override suspend fun insertNote(note: Note): Result<Long> =
        runCatching { noteDao.insertNote(note) }

    override suspend fun updateNote(note: Note): Result<Int> =
        runCatching { noteDao.updateNote(note) }

    override suspend fun deleteNote(note: Note): Result<Int> =
        runCatching { noteDao.deleteNote(note) }

    override suspend fun deleteNoteById(id: Long): Result<Int> =
        runCatching { noteDao.deleteNoteById(id) }
}
