package com.opennotes.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotesViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _categories = MutableStateFlow(
        listOf(
            Category("1", "Personal", Color(0xFF2196F3)), // Blue
            Category("2", "Work", Color(0xFFF44336)),     // Red
            Category("3", "Ideas", Color(0xFF4CAF50)),    // Green
            Category("4", "Tasks", Color(0xFFFF9800))     // Orange
        )
    )
    val categories: StateFlow<List<Category>> = _categories

    private val _notes = MutableStateFlow(
        listOf(
            Note("1", "Shopping List", "Milk, eggs, bread...", "1", true),
            Note("2", "Meeting Notes", "Discuss project timeline...", "2", true),
            Note("3", "Birthday Ideas", "Gift options for Sarah...", "3"),
            Note("4", "Weekend Plans", "Visit the park, dinner with friends...", "1"),
            Note("5", "Project Deadlines", "Submit report by Friday...", "2"),
            Note("6", "Book Recommendations", "1984, Dune, Foundation...", "3"),
            Note("7", "Home Repairs", "Fix kitchen sink...", "4"),
            Note("8", "Travel Plans", "Book flights for vacation...", "1")
        )
    )
    val notes: StateFlow<List<Note>> = _notes

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}