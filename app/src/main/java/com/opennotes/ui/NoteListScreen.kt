package com.opennotes.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opennotes.ui.theme.BackgroundColor
import com.opennotes.ui.theme.DarkBackgroundColor
import com.opennotes.ui.theme.BlackUnselected
import com.opennotes.ui.theme.LightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(viewModel: NotesViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }
    val isDarkMode = viewModel.isDarkMode.value
    Log.d("NotesUI", "Notes in UI: ${notes.size}")
    Log.d("NotesUI", "Categories in UI: ${categories.size}")

    // Theme-aware colors
    val backgroundColor = if (isDarkMode) DarkBackgroundColor else BackgroundColor
    val textColor = if (isDarkMode) Color.White else Color.Black
    val searchFieldColor = if (isDarkMode) Color.DarkGray else LightGray
    val placeholderColor = if (isDarkMode) Color.LightGray else Color.Gray
    val dividerColor = if (isDarkMode) Color.DarkGray else Color.LightGray
    val cardBackgroundColor = if (isDarkMode) Color(0xFF2D2D2D) else Color.White
    val noteContentColor = if (isDarkMode) Color.LightGray else Color.Gray
    val iconTint = if (isDarkMode) Color.LightGray else Color.DarkGray


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp)),
            placeholder = { Text("Search notes...", color = placeholderColor) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = iconTint
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = searchFieldColor,
                focusedTextColor = textColor,
                cursorColor = textColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Notes content
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Pinned Notes Section
            val pinnedNotes = notes.filter { it.isPinned }
            if (pinnedNotes.isNotEmpty() && searchQuery.isEmpty()) {
                item {
                    Text(
                        text = "Pinned",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = textColor
                    )
                }

                items(pinnedNotes) { note ->
                    val category = categories.find { it.id == note.categoryId }
                    NoteCard(note, 
                             category?.color ?: Color.Gray, 
                             isDarkMode, cardBackgroundColor, 
                             noteContentColor, 
                             onPin = { viewModel.toggleNotePin(it.id) },
                             onDelete = { viewModel.deleteNote(it.id) })

                }

                item {
                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = dividerColor
                    )
                }
            }

            // Filter categories based on search if needed
            val filteredCategories = if (searchQuery.isEmpty()) {
                categories
            } else {
                categories.filter { category ->
                    notes.any { note ->
                        note.categoryId == category.id &&
                                (note.title.contains(searchQuery, ignoreCase = true) ||
                                        note.content.contains(searchQuery, ignoreCase = true))
                    }
                }
            }

            // Categories and their notes
            filteredCategories.forEach { category ->
                val categoryNotes = if (searchQuery.isEmpty()) {
                    notes.filter { it.categoryId == category.id && !it.isPinned }
                } else {
                    notes.filter {
                        it.categoryId == category.id &&
                                (it.title.contains(searchQuery, ignoreCase = true) ||
                                        it.content.contains(searchQuery, ignoreCase = true))
                    }
                }

                if (categoryNotes.isNotEmpty()) {
                    item {
                        CategoryHeader(
                            category = category,
                            isExpanded = expandedCategories[category.id] ?: false,
                            onToggle = {
                                expandedCategories[category.id] = !(expandedCategories[category.id] ?: false)
                            }
                        )
                    }

                    if (expandedCategories[category.id] == true) {
                        items(categoryNotes) { note ->
                            NoteCard(note, 
                                     category.color, 
                                     isDarkMode, 
                                     cardBackgroundColor, 
                                     noteContentColor, 
                                     onPin = { viewModel.toggleNotePin(it.id) },
                                     onDelete = { viewModel.deleteNote(it.id) })
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CategoryHeader(
    category: Category,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category.name,
            color = category.color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = if (isExpanded) "▼" else "▶",
            color = category.color,
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NoteCard(
    note: Note,
    categoryColor: Color,
    isDarkMode: Boolean,
    cardBackgroundColor: Color,
    noteContentColor: Color,
    onPin: (Note) -> Unit,  // Add callback for pin action
    onDelete: (Note) -> Unit  // Add callback for delete action
) {
    val visibleState = remember(note.id) { MutableTransitionState(false) }
    var showDialog by remember { mutableStateOf(false) }

    // Start the animation once per note ID
    LaunchedEffect(note.id) {
        visibleState.targetState = true
    }

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .combinedClickable(
                    onClick = { /* Regular click - you could navigate to note detail */ },
                    onLongClick = { showDialog = true }  // Show dialog on long press
                ),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Preview snippet of note
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = note.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = categoryColor,
                            modifier = Modifier.weight(1f)
                        )

                        if (note.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = categoryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (note.content.length > 50) note.content.take(50) + "..." else note.content,
                        fontSize = 14.sp,
                        color = noteContentColor
                    )
                }
            }
        }
    }

    // Long press dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = BlackUnselected)
                }
            },

            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Pin Button
                    TextButton(
                        onClick = {
                            onPin(note)
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = if (note.isPinned) "Unpin" else "Pin"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (note.isPinned) "Unpin" else "Pin", color = BlackUnselected)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    // Delete Button
                    TextButton(
                        onClick = {
                            onDelete(note)
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Delete", color = BlackUnselected)
                        }
                    }
                }
            }
        )
    }
}