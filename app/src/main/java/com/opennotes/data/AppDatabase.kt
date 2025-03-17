package com.opennotes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.opennotes.data.dao.CategoryDao
import com.opennotes.data.dao.NoteDao
import com.opennotes.data.entities.Category
import com.opennotes.data.entities.Note

@Database(entities = [Category::class, Note::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun noteDao(): NoteDao
}