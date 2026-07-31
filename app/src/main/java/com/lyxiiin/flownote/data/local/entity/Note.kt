package com.lyxiiin.flownote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val NOTE_TABLE = "notes"

@Entity(tableName = NOTE_TABLE)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** 笔记标题 */
    @ColumnInfo(name = "title")
    val title: String,

    /** 笔记内容 */
    @ColumnInfo(name = "content")
    val content: String = "",

    /** 所属分组 ID，null 表示未分组 */
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,

    /** 创建时间戳（毫秒） */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /** 更新时间戳（毫秒） */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
