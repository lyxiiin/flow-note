package com.lyxiiin.flownote

import android.app.Application
import com.lyxiiin.flownote.data.local.AppDatabase
import com.lyxiiin.flownote.data.repository.NoteCategoryRepository
import com.lyxiiin.flownote.data.repository.NoteCategoryRepositoryImpl
import com.lyxiiin.flownote.data.repository.NoteRepository
import com.lyxiiin.flownote.data.repository.NoteRepositoryImpl
import com.lyxiiin.flownote.data.repository.TodoRepository
import com.lyxiiin.flownote.data.repository.TodoRepositoryImpl

class FnApplication: Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
    val noteCategoryRepository: NoteCategoryRepository by lazy {
        NoteCategoryRepositoryImpl(database.noteCategoriesDao())
    }

    val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(database.noteDao())
    }

    val todoRepository: TodoRepository by lazy {
        TodoRepositoryImpl(database.todoDao())
    }
}