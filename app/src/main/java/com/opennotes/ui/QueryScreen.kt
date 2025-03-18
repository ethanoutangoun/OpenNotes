package com.opennotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.opennotes.ui.theme.LightGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryScreen(viewModel: NotesViewModel) {
    val queryInput by viewModel.queryInput.collectAsState()
    val queryResult by viewModel.queryResult.collectAsState()
    val isDarkMode = viewModel.isDarkMode.value

    // Theme-aware colors
    val backgroundColor = if (isDarkMode) DarkBackgroundColor else BackgroundColor
    val textColor = if (isDarkMode) Color.White else Color.Black
    val searchFieldColor = if (isDarkMode) Color.DarkGray else LightGray
    val placeholderColor = if (isDarkMode) Color.LightGray else Color.Gray
    val resultBackgroundColor = if (isDarkMode) Color(0xFF2D2D2D) else Color(0xFFF6F6F6)
    val resultTextColor = if (isDarkMode) Color.White else Color.Black
    val noResultsTextColor = if (isDarkMode) Color.LightGray else Color.Gray
    val iconTint = if (isDarkMode) Color.LightGray else Color.DarkGray

    // State to track if search has been triggered
    var isSearching by remember { mutableStateOf(false) }

    // CoroutineScope to launch the suspending function
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Search Bar
        TextField(
            value = queryInput,
            onValueChange = { viewModel.updateQueryInput(it)},
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
                cursorColor = textColor,
                focusedTextColor = resultTextColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Button
        Button(
            onClick = {
                isSearching = true
                // Launch the queryNotes suspending function within a coroutine
                coroutineScope.launch {
                    viewModel.queryNotes(queryInput) // Trigger query when button is clicked
                    isSearching = false // Reset isSearching after the search is completed
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDarkMode) Color.DarkGray else MaterialTheme.colorScheme.primary,
                contentColor = if (isDarkMode) Color.White else Color.White,
                disabledContainerColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.LightGray,
                disabledContentColor = if (isDarkMode) Color.Gray else Color.DarkGray
            ),
            enabled = queryInput.isNotEmpty() && !isSearching
        ) {
            Text(text = "Search")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Show loading indicator while searching
        if (isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.primary
                )
            }
        } else {
            // Display query result
            if (queryResult.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(resultBackgroundColor, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = queryResult,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = resultTextColor
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No results found",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = noResultsTextColor
                )
            }
        }
    }
}