package com.lyxiiin.flownote.data.repository

import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import kotlinx.coroutines.flow.Flow

interface NoteCategoryRepository {
    fun getAllNoteCategories(): Flow<List<NoteCategory>>
    fun getAllCategoriesWithCount(): Flow<List<NoteCategoryWithCount>>
    fun getCategoryById(id: Long): Flow<NoteCategory?>
    suspend fun insertNoteCategory(noteCategory: NoteCategory): Result<Long>
    suspend fun updateNoteCategory(noteCategory: NoteCategory): Result<Int>
    suspend fun deleteNoteCategory(noteCategory: NoteCategory): Result<Int>
    suspend fun deleteNoteCategoryById(id: Long): Result<Int>
    suspend fun isNameExists(name: String): Boolean
}
