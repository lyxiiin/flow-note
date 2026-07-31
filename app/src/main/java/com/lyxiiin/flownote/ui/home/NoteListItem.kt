package com.lyxiiin.flownote.ui.home

import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount

sealed class NoteListItem {
    data class Category(val categoryWithCount: NoteCategoryWithCount): NoteListItem()
    data class UngroupedNote(val note: Note): NoteListItem()
}