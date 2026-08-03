package com.lyxiiin.flownote.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NoteCategoryDetailViewModel(
    private val categoryId: Long,
    private val categoryRepository: NoteCategoryRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {
    val category: LiveData<NoteCategory?> =
        categoryRepository.getCategoryById(categoryId).asLiveData()

    val notes: LiveData<List<Note>> =
        noteRepository.getNoteByCategory(categoryId).asLiveData()

    fun deleteCategory() {
        viewModelScope.launch {
            categoryRepository.deleteNoteCategoryById(categoryId)
        }
    }
    fun renameCategory(category: NoteCategory){
        viewModelScope.launch {
            categoryRepository.updateNoteCategory(category)
        }
    }
    fun dismissCategory(){
        viewModelScope.launch {
            val notes = noteRepository.getNoteByCategory(categoryId).first()
            for (note in notes){
                noteRepository.updateNote(note.copy(categoryId = null))
            }
            categoryRepository.deleteNoteCategoryById(categoryId)
        }
    }

    fun renameNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(id: Long){
        viewModelScope.launch {
            noteRepository.deleteNoteById(id)
        }
    }
}