package com.opennotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.opennotes.ui.theme.BackgroundColor
import com.opennotes.ui.theme.LightGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryScreen(viewModel: NotesViewModel) {
    val queryInput by viewModel.queryInput.collectAsState()
    val queryResult by viewModel.queryResult.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // State to track if search has been triggered
    var isSearching by remember { mutableStateOf(false) }

    // CoroutineScope to launch the suspending function
    val coroutineScope = rememberCoroutineScope()

    // Function to handle search action
    val performSearch = {
        if (queryInput.isNotEmpty() && !isSearching) {
            isSearching = true
            keyboardController?.hide() // Hide keyboard after search is triggered
            coroutineScope.launch {
                viewModel.queryNotes(queryInput)
                isSearching = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .padding(16.dp)
    ) {
        // Search Bar with keyboard actions
        TextField(
            value = queryInput,
            onValueChange = { viewModel.updateQueryInput(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp)),
            placeholder = { Text("Search notes...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon"
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            // Add keyboard options and actions
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                performSearch()
            })
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Button
        Button(
            onClick = { performSearch() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
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
                CircularProgressIndicator()
            }
        } else {
            // Display query result
            if (queryResult.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = queryResult,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No results found",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}