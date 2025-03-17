package com.opennotes.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.opennotes.data.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.opennotes.BuildConfig

class NotesViewModel : ViewModel() {

    private val model = Model()
    private val apiKey = BuildConfig.OPENAI_API_KEY




    private val _queryInput = MutableStateFlow("")
    val queryInput: StateFlow<String> = _queryInput

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _queryResult = MutableStateFlow("")
    val queryResult: StateFlow<String> = _queryResult


    private val _categories = MutableStateFlow(
        listOf(
            Category("1", "Grocery", Color(0xFF2196F3)), // Blue
//            Category("2", "Work", Color(0xFFF44336)),     // Red
//            Category("3", "Ideas", Color(0xFF4CAF50)),    // Green
//            Category("4", "Tasks", Color(0xFFFF9800))     // Orange
        )
    )
    val categories: StateFlow<List<Category>> = _categories

    private val _notes = MutableStateFlow(
        listOf(
            Note("1", "Shopping List", "Milk, eggs, bread...", "1", true),
//            Note("2", "Meeting Notes", "Discuss project timeline...", "2", true),
//            Note("3", "Birthday Ideas", "Gift options for Sarah...", "3"),
//            Note("4", "Weekend Plans", "Visit the park, dinner with friends...", "1"),
//            Note("5", "Project Deadlines", "Submit report by Friday...", "2"),
//            Note("6", "Book Recommendations", "1984, Dune, Foundation...", "3"),
//            Note("7", "Home Repairs", "Fix kitchen sink...", "4"),
//            Note("8", "Travel Plans", "Book flights for vacation...", "1")
        )
    )
    val notes: StateFlow<List<Note>> get() = _notes

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateQueryInput(query: String){
        _queryInput.value = query
    }

    fun String.removePrefix(prefix: String): String {
        return if (this.startsWith(prefix)) {
            this.substring(prefix.length)
        } else {
            this
        }
    }

    suspend fun addNote(content: String) {
        // Log that the function is being called
        Log.d("NotesViewModel", "Adding note: $content")

        try {
            val (categoryName, categoryColor) = model.categorizeSingleNote(
                apiKey = apiKey,
                noteContent = content,
                existingCategories = _categories.value
            )

            // Log the API response
            Log.d("NotesViewModel", "API Response - Category: $categoryName, Color: $categoryColor")

            var categoryId = _categories.value.find { it.name == categoryName }?.id

            if (categoryId == null) {
                val newCategoryId = java.util.UUID.randomUUID().toString()
                val newCategory = Category(newCategoryId, categoryName, categoryColor)

                addCategory(newCategory)
                categoryId = newCategoryId

                Log.d("NotesViewModel", "New category added: $categoryName with ID: $categoryId")
            }

            val newNote = Note(java.util.UUID.randomUUID().toString(), content, content, categoryId, false)

            _notes.value += newNote

            Log.d("NotesViewModel", "Note added successfully: ${newNote.id}")
            Log.d("NotesViewModel", "Notes after adding: ${_notes.value}")

        } catch (e: Exception) {
            Log.e("NotesViewModel", "Error while categorizing note", e)
        }

        return
    }

    fun deleteNote(noteId: String) {
        _notes.value = _notes.value.filter { it.id != noteId }
    }

    // Similar functions for categories
    fun addCategory(category: Category) {
        val currentCategories = _categories.value.toMutableList()
        currentCategories.add(category)
        _categories.value = currentCategories
    }

    suspend fun queryNotes(query: String) {
        val noteContext = _notes.value.joinToString(" ") { it.content }

        try {
            // Call the model to query the notes
            val result = model.queryWithContext(apiKey, query, noteContext)

            // Update the query result in MutableStateFlow
            _queryResult.value = result

            Log.d("NotesViewModel", "Query Result: $result")

        } catch (e: Exception) {
            Log.e("NotesViewModel", "Error while querying notes", e)
        }
    }
}