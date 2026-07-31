package com.lyxiiin.flownote.data.repository

import com.lyxiiin.flownote.data.local.dao.NoteCategoriesDao
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import kotlinx.coroutines.flow.Flow

class NoteCategoryRepositoryImpl(
    private val noteCategoriesDao: NoteCategoriesDao
) : NoteCategoryRepository {

    override fun getAllNoteCategories(): Flow<List<NoteCategory>> =
        noteCategoriesDao.getAllNoteCategories()

    override fun getAllCategoriesWithCount(): Flow<List<NoteCategoryWithCount>> =
        noteCategoriesDao.getAllCategoriesWithCount()

    override fun getCategoryById(id: Long): Flow<NoteCategory?> =
        noteCategoriesDao.getCategoryById(id)

    override suspend fun insertNoteCategory(noteCategory: NoteCategory): Result<Long> =
        runCatching { noteCategoriesDao.insertNoteCategory(noteCategory) }

    override suspend fun updateNoteCategory(noteCategory: NoteCategory): Result<Int> =
        runCatching { noteCategoriesDao.updateNoteCategory(noteCategory) }

    override suspend fun deleteNoteCategory(noteCategory: NoteCategory): Result<Int> =
        runCatching { noteCategoriesDao.deleteNoteCategory(noteCategory) }

    override suspend fun deleteNoteCategoryById(id: Long): Result<Int> =
        runCatching { noteCategoriesDao.deleteNoteCategoryById(id) }

    override suspend fun isNameExists(name: String): Boolean =
        noteCategoriesDao.isNameExists(name)
}
