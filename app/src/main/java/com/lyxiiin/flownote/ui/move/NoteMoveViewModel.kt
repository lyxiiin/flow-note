package com.lyxiiin.flownote.ui.move

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategoryWithCount
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NoteMoveViewModel(
    private val noteId: Long,
    private val noteRepository: NoteRepository,
    private val repository: NoteCategoryRepository
): ViewModel() {
    val categories: LiveData<List<NoteCategoryWithCount>> =
        repository.getAllCategoriesWithCount().asLiveData()

    val note: LiveData<Note?> = noteRepository.getNoteById(noteId).asLiveData()

    /** 移动结果一次性事件：true 表示移动成功 */
    private val _moveResult = Channel<Boolean>(Channel.BUFFERED)
    val moveResult: Flow<Boolean> = _moveResult.receiveAsFlow()

    fun changeNoteCategory(id: Long?){
        viewModelScope.launch {
            val success = runCatching {
                noteRepository.getNoteById(noteId).first()?.let {
                    noteRepository.updateNote(
                        it.copy(categoryId = id, updatedAt = System.currentTimeMillis())
                    )
                    true
                } ?: false
            }.getOrDefault(false)
            _moveResult.send(success)
        }
    }
}