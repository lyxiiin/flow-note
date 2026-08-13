package com.lyxiiin.flownote.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lyxiiin.flownote.R
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import com.lyxiiin.flownote.data.local.entity.Todo
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteRepository
import com.lyxiiin.flownote.data.repository.TodoRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    // Todo页
    val todoList: LiveData<List<TodoListItem>> = combine(todoRepository.getTodayTodos(),todoRepository.getTomorrowTodos(), todoRepository.getLaterTodos(), todoRepository.getTodosByState(true)){ today, tomorrow, later,done ->
        listOf(
            TodoGroup(R.string.group_today,today),
            TodoGroup(R.string.group_tomorrow,tomorrow),
            TodoGroup(R.string.group_later,later),
            TodoGroup(R.string.group_done,done)
        ).filter { it.todos.isNotEmpty() }
    }
        .map { flattenGroups(it) }
        .asLiveData()


    private fun flattenGroups(groups: List<TodoGroup>): List<TodoListItem> =
        groups.flatMap { group ->
            listOf(TodoListItem.Header(group.titleRes, group.todos.size)) +
                    group.todos.map { TodoListItem.TodoRow(it) }
        }
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

    fun deleteTodo(id: Long) {
        viewModelScope.launch {
            todoRepository.deleteTodoById(id)
        }
    }

    fun updateTodo(todo: Todo){
        viewModelScope.launch {
            todoRepository.updateTodo(todo.copy(isDone = true, updatedAt = System.currentTimeMillis()))
        }
    }
}