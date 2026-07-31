package com.lyxiiin.flownote.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lyxiiin.flownote.data.repository.NoteRepository

class NoteDetailViewModelFactory(
    private val noteId: Long,
    private val categoryId: Long,
    private val noteRepository: NoteRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteDetailViewModel::class.java)){
            return NoteDetailViewModel(noteId,categoryId,noteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}