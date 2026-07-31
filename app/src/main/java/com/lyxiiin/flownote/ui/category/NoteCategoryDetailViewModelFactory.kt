package com.lyxiiin.flownote.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteRepository

class NoteCategoryDetailViewModelFactory(
    private val categoryId: Long,
    private val categoryRepository: NoteCategoryRepository,
    private val noteRepository: NoteRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NoteCategoryDetailViewModel::class.java)){
            return NoteCategoryDetailViewModel(categoryId,categoryRepository,noteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}