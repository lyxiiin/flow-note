package com.lyxiiin.flownote.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lyxiiin.flownote.data.local.dao.NoteCategoriesDao
import com.lyxiiin.flownote.data.local.dao.NoteDao
import com.lyxiiin.flownote.data.local.dao.TodoDao
import com.lyxiiin.flownote.data.local.entity.Note
import com.lyxiiin.flownote.data.local.entity.NoteCategory
import com.lyxiiin.flownote.data.local.entity.Todo

@Database(
    entities = [
        NoteCategory::class,
        Note::class,
        Todo::class,
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteCategoriesDao(): NoteCategoriesDao
    abstract fun noteDao(): NoteDao
    abstract fun todoDao(): TodoDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flow_note_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
