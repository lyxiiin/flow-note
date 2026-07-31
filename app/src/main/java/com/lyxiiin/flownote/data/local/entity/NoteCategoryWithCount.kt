package com.lyxiiin.flownote.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/** 分组 + 组内笔记数量（DAO 查询结果载体，非 Entity） */
data class NoteCategoryWithCount(
    @Embedded val category: NoteCategory,
    @ColumnInfo(name = "note_count") val noteCount: Int
)
