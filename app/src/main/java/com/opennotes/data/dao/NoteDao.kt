package com.opennotes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import com.opennotes.data.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insertNote(note: com.opennotes.data.entities.Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes WHERE content LIKE :query")
    fun searchNotes(query: String): Flow<List<Note>>

    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<Note>>
}