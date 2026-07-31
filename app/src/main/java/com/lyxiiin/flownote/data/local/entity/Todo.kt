package com.lyxiiin.flownote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TODO_TABLE = "todos"

@Entity(tableName = TODO_TABLE)
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** 待办标题 */
    @ColumnInfo(name = "title")
    val title: String,

    /** 详细描述 */
    @ColumnInfo(name = "description")
    val description: String = "",

    /** 是否已完成 */
    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,

    /** 截止日期时间戳（毫秒），null 表示无截止日期 */
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null,

    /** 创建时间戳（毫秒） */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /** 更新时间戳（毫秒） */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
