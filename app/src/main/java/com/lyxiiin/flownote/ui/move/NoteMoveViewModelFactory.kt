package com.lyxiiin.flownote.ui.move

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteRepository

class NoteMoveViewModelFactory(
    private val noteId: Long,
    private val noteRepository: NoteRepository,
    private val categoryRepository: NoteCategoryRepository
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteMoveViewModel::class.java)) {
            return NoteMoveViewModel(noteId, noteRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}