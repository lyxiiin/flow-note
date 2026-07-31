package com.lyxiiin.flownote.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.repository.NoteRepository
import com.lyxiiin.flownote.util.toDateTimeString
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val noteId: Long,
    private val categoryId: Long,
    private val noteRepository: NoteRepository
) : ViewModel(){
    val isNewNote = noteId == -1L

    private val _title = MutableLiveData("")
    private val _content = MutableLiveData("")

    var note: LiveData<Note?> = if(isNewNote){
        MutableLiveData(null)
    }else{
        noteRepository.getNoteById(noteId).asLiveData()
    }


    fun onExit() {
        val title = _title.value.orEmpty().trim()
        val content = _content.value.orEmpty().trim()

        if (title.isEmpty() && content.isEmpty()){
            return
        }

        viewModelScope.launch(NonCancellable) {
            if (isNewNote){
                if (categoryId != -1L){
                    noteRepository.insertNote(Note(title = title, content = content, categoryId = categoryId))
                }else{
                    noteRepository.insertNote(Note(title = title, content = content))
                }
            } else {
                note.value?.let {
                    noteRepository.updateNote(it.copy(title = title, content = content, updatedAt = System.currentTimeMillis()))
                }
            }
        }
    }
    fun updateTitle(title: String) {
        _title.value = title
    }

    fun updateContent(content: String) {
        _content.value = content
    }
    fun deleteNote(){
        viewModelScope.launch {
            noteRepository.deleteNoteById(noteId)
        }
    }

}