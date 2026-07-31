package com.lyxiiin.flownote.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteRepository

class HomeViewModelFactory(
    private val categoryRepository: NoteCategoryRepository,
    private val noteRepository: NoteRepository
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(categoryRepository, noteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}