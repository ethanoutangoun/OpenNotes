package com.opennotes.ui

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.opennotes.data.DatabaseHelper
import com.opennotes.data.Model
import com.opennotes.data.entities.Category as RoomCategory
import com.opennotes.data.entities.Note as RoomNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser


class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val model = Model()
    private val apiKey = ""

    // Database access
    private val database = DatabaseHelper.getDatabase(application)
    private val noteDao = database.noteDao()
    private val categoryDao = database.categoryDao()

    private val _queryInput = MutableStateFlow("")
    val queryInput: StateFlow<String> = _queryInput

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _queryResult = MutableStateFlow("")
    val queryResult: StateFlow<String> = _queryResult

    // Initial dummy data - will implement DB loading later
    private val _categories = MutableStateFlow(
        listOf(
            Category("1", "Grocery", Color(0xFF2196F3)), // Blue
        )
    )
    val categories: StateFlow<List<Category>> = _categories

    private val _notes = MutableStateFlow(
        listOf(
            Note("1", "Shopping List", "Milk, eggs, bread...", "1", true),
        )
    )
    val notes: StateFlow<List<Note>> get() = _notes

    // Initialize database if needed
    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Load initial data
            loadCategoriesFromDb()
            loadNotesFromDb()
        }
    }

    private suspend fun loadCategoriesFromDb() {
        try {
            val roomCategories = categoryDao.getAllCategories().stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                emptyList()
            ).value

            if (roomCategories.isNotEmpty()) {
                val uiCategories = roomCategories.map { roomCategory ->
                    // Convert hex color string to Color
                    val colorInt = try {
                        android.graphics.Color.parseColor(roomCategory.colorHex)
                    } catch (e: Exception) {
                        0xFF2196F3.toInt() // Default blue if parsing fails
                    }

                    Category(
                        id = roomCategory.id,
                        name = roomCategory.name,
                        color = Color(colorInt)
                    )
                }
                _categories.value = uiCategories
            } else {
                // DB is empty, save our initial data to DB
                _categories.value.forEach { category ->
                    val roomCategory = RoomCategory(
                        id = category.id,
                        name = category.name,
                        colorHex = "#${category.color.value.toString(16)}"
                    )
                    categoryDao.insertCategory(roomCategory)
                }
            }
        } catch (e: Exception) {
            Log.e("NotesViewModel", "Error loading categories from DB", e)
        }
    }

    private suspend fun loadNotesFromDb() {
        try {
            val roomNotes = noteDao.getAllNotes().stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                emptyList()
            ).value

            if (roomNotes.isNotEmpty()) {
                val uiNotes = roomNotes.map { roomNote ->
                    // Extract title from content (first line or first 30 chars)
                    val title = roomNote.content.split("\n").firstOrNull() ?:
                    if (roomNote.content.length > 30)
                        roomNote.content.substring(0, 30) + "..."
                    else
                        roomNote.content

                    Note(
                        id = roomNote.id,
                        title = title,
                        content = roomNote.content,
                        categoryId = roomNote.categoryId.toString(),
                    )
                }
                _notes.value = uiNotes
            } else {
                // DB is empty, save our initial data to DB
                _notes.value.forEach { note ->
                    val roomNote = RoomNote(
                        id = note.id,
                        content = note.content,
                        categoryId = note.categoryId
                    )
                    noteDao.insertNote(roomNote)
                }
            }
        } catch (e: Exception) {
            Log.e("NotesViewModel", "Error loading notes from DB", e)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query



//        Whenever I type, it deletes all notes so I removed below logic, not sure what this is doing

//        // Also update the filtered notes based on search query
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                if (query.isNotEmpty()) {
//                    val searchResults = noteDao.searchNotes("%$query%").stateIn(
//                        viewModelScope,
//                        SharingStarted.Eagerly,
//                        emptyList()
//                    ).value
//
//                    val uiResults = searchResults.map { roomNote ->
//                        val title = roomNote.content.split("\n").firstOrNull() ?:
//                        if (roomNote.content.length > 30)
//                            roomNote.content.substring(0, 30) + "..."
//                        else
//                            roomNote.content
//
//                        Note(
//                            id = roomNote.id,
//                            title = title,
//                            content = roomNote.content,
//                            categoryId = roomNote.categoryId.toString()
//                        )
//                    }
//                    _notes.value = uiResults
//                } else {
//                    // If empty query, load all notes
//                    loadNotesFromDb()
//                }
//            } catch (e: Exception) {
//                Log.e("NotesViewModel", "Error searching notes", e)
//            }
//        }
    }

    fun updateQueryInput(query: String){
        _queryInput.value = query
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
                val newCategoryId = UUID.randomUUID().toString()
                val newCategory = Category(newCategoryId, categoryName, categoryColor)

                // Add to UI state
                addCategory(newCategory)
                categoryId = newCategoryId
                Log.d("NotesViewModel", "Adding New Category: $categoryName with ID: $categoryId")

                // Add to database
                val roomCategory = RoomCategory(
                    id = newCategoryId,
                    name = categoryName,
                    colorHex = "#${categoryColor.value.toString(16)}"
                )
                Log.d("NotesViewModel", "Made it here")
                withContext(Dispatchers.IO) {
                    categoryDao.insertCategory(roomCategory)
                }
                Log.d("NotesViewModel", "Also here (shouldn't get here)")

                Log.d("NotesViewModel", "New category added: $categoryName with ID: $categoryId")
            }

            val newNoteId = UUID.randomUUID().toString()
            val title = content.split("\n").firstOrNull() ?:
            if (content.length > 30) content.substring(0, 30) + "..." else content

            // Add to UI state
            val newUiNote = Note(newNoteId, title, content, categoryId, false)
            _notes.value += newUiNote

            // Add to database
            val roomNote = RoomNote(
                id = newNoteId,
                content = content,
                categoryId = categoryId
            )
            withContext(Dispatchers.IO) {
                noteDao.insertNote(roomNote)
            }

            Log.d("NotesViewModel", "Note added successfully: $newNoteId")

        } catch (e: Exception) {
            Log.e("NotesViewModel", "Error while categorizing note", e)
        }
    }

    private fun deleteNote(noteId: String) {
        // Remove from UI state
        _notes.value = _notes.value.filter { it.id != noteId }

        // Remove from database
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val noteToDelete = noteDao.getAllNotes().stateIn(
                    viewModelScope,
                    SharingStarted.Eagerly,
                    emptyList()
                ).value.find { it.id == noteId }

                noteToDelete?.let {
                    noteDao.deleteNote(it)
                    Log.d("NotesViewModel", "Note deleted from database: $noteId")
                }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Error deleting note from database", e)
            }
        }
    }

    // Similar functions for categories
    fun addCategory(category: Category) {
        // Add to UI state
        val currentCategories = _categories.value.toMutableList()
        currentCategories.add(category)
        _categories.value = currentCategories

        // Add to database
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val roomCategory = RoomCategory(
                    id = category.id,
                    name = category.name,
                    colorHex = "#${category.color.value.toString(16)}"
                )
                categoryDao.insertCategory(roomCategory)
                Log.d("NotesViewModel", "Category added to database: ${category.id}")
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Error adding category to database", e)
            }
        }
    }

    suspend fun queryNotes(query: String) {
        val noteContext = _notes.value.joinToString(" ") {
            "({id: ${it.id}, title: ${it.title}, content: ${it.content}, categoryId: ${it.categoryId}})"
        }
        Log.d("NotesViewModel", noteContext)

        try {
            // Call the model to query the notes
            val result = model.queryWithContext(apiKey, query, noteContext)
            val response = result["response"]
            val functionCall = result["type"] == "function"


            // Check if function call occurs
            if (functionCall) {
                // If it's a function call, you have the function name and parameters
                val functionName = result["function_name"] as String
                val arguments = result["params"]



                Log.d("FunctionCall", "Function Name: $functionName")
                Log.d("FunctionCall", "Arguments: $arguments")

                val jsonObject = try {
                    JsonParser.parseString(arguments.toString()).asJsonObject
                } catch (e: Exception) {
                    Log.e("FunctionCall", "Error parsing arguments: $e")
                    null
                }

                jsonObject?.let {
                    when (functionName) {
                        "delete_note" -> {
                            try {
                                val noteId = it.get("note_id").asString
                                // Perform the delete note operation
                                deleteNote(noteId)
                            } catch (e: Exception) {
                                Log.e("FunctionCall", "Error extracting note_id: $e")
                            }
                        }
                        // Handle other function cases
                        else -> {
                            Log.d("FunctionCall", "Unknown function: $functionName")
                        }
                    }
                }
            }

            // Update the response
            _queryResult.value = response.toString()

            Log.d("NotesViewModel", "Query Result: $result")

        } catch (e: Exception) {
            Log.e("NotesViewModel", "Error while querying notes", e)
        }
    }
}