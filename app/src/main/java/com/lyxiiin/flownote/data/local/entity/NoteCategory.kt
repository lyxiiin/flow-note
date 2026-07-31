package com.lyxiiin.flownote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val NOTE_CATEGORIES_TABLE = "note_categories"

@Entity(tableName = NOTE_CATEGORIES_TABLE)
data class NoteCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** 分组名称 */
    @ColumnInfo(name = "name")
    val name: String,

    /** 创建时间戳（毫秒） */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /** 更新时间戳（毫秒） */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
