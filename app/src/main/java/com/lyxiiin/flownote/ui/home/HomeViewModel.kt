package com.lyxiiin.flownote.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteRepository
import com.lyxiiin.flownote.data.repository.TodoRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class InsertResult {
    object Success : InsertResult()
    object Duplicate : InsertResult()
    object Empty : InsertResult()
}

class HomeViewModel(
    private val noteCategoryRepository: NoteCategoryRepository,
    private val noteRepository: NoteRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {
    val allNoteCategories: LiveData<List<NoteCategoryWithCount>> = noteCategoryRepository.getAllCategoriesWithCount().asLiveData()

    val ungroupedNote: LiveData<List<Note>> = noteRepository.getUngroupedNotes().asLiveData()
    private val _insertResult = Channel<InsertResult>(Channel.BUFFERED)
    val insertResult: Flow<InsertResult> = _insertResult.receiveAsFlow()
//
//    // Todo页
//    val todoGroups = combine(todoRepository.getTodayTodos(),todoRepository.getTomorrowTodos(), todoRepository.getLaterTodos()){ today,tomorrow,later ->
//        listOf(
//            TodoGroup("今天",today),
//            TodoGroup("明天",tomorrow),
//            TodoGroup("更远",later),
//        )
//    }.asLiveData()

    fun insertCategory(name: String) {
        if (name.isBlank()) {
            _insertResult.trySend(InsertResult.Empty)
            return
        }
        viewModelScope.launch {
            if (noteCategoryRepository.isNameExists(name)) {
                _insertResult.send(InsertResult.Duplicate)
                return@launch
            }
            noteCategoryRepository.insertNoteCategory(NoteCategory(name = name))
                .onSuccess { _insertResult.send(InsertResult.Success) }
                .onFailure { _insertResult.send(InsertResult.Duplicate) }
        }
    }
    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            noteCategoryRepository.deleteNoteCategoryById(id)
        }
    }
    fun renameCategory(category: NoteCategory){
        viewModelScope.launch {
            noteCategoryRepository.updateNoteCategory(category)
        }
    }
    fun dismissCategory(category: NoteCategory){
        viewModelScope.launch {
            val notes = noteRepository.getNoteByCategory(category.id).first()
            for (note in notes){
                noteRepository.updateNote(note.copy(categoryId = null))
            }
            noteCategoryRepository.deleteNoteCategoryById(category.id)
        }
    }

    fun renameNote(note: Note) {
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            noteRepository.deleteNoteById(id)
        }
    }
}