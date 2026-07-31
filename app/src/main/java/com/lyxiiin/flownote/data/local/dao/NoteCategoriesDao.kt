package com.lyxiiin.flownote.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteCategoriesDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNoteCategory(noteCategory: NoteCategory): Long

    @Update
    suspend fun updateNoteCategory(noteCategory: NoteCategory): Int

    @Delete
    suspend fun deleteNoteCategory(noteCategory: NoteCategory): Int

    @Query("DELETE FROM note_categories WHERE id = :id")
    suspend fun deleteNoteCategoryById(id: Long): Int

    @Query("SELECT * FROM note_categories ORDER BY created_at DESC")
    fun getAllNoteCategories(): Flow<List<NoteCategory>>

    @Query("""
        SELECT c.*, COUNT(n.id) AS note_count
        FROM note_categories c
        LEFT JOIN notes n ON n.category_id = c.id
        GROUP BY c.id
        ORDER BY c.created_at DESC
    """)
    fun getAllCategoriesWithCount(): Flow<List<NoteCategoryWithCount>>

    @Query("SELECT * FROM note_categories WHERE id = :id")
    fun getCategoryById(id: Long): Flow<NoteCategory?>

    @Query("SELECT COUNT(*) > 0 FROM note_categories WHERE LOWER(name) = LOWER(:name)")
    suspend fun isNameExists(name: String): Boolean
}
