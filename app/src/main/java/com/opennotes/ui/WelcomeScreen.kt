package com.opennotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opennotes.ui.theme.BackgroundColor
import com.opennotes.ui.theme.DarkBackgroundColor
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(notesViewModel: NotesViewModel) {
    var text by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val isDarkMode = notesViewModel.isDarkMode.value

    // Theme-aware colors
    val backgroundColor = if (isDarkMode) DarkBackgroundColor else BackgroundColor
    val textColor = if (isDarkMode) Color.White else Color.Black
    val placeholderColor = if (isDarkMode) Color.LightGray else Color.Gray

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Open Notes ✏️",
            fontSize = 24.sp,
            color = textColor
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Borderless TextField with placeholder
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .padding(8.dp)
                .width(280.dp)
                .height(120.dp),
            textStyle = TextStyle(fontSize = 18.sp, color = textColor),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(backgroundColor)
                        .padding(8.dp)
                ) {
                    if (text.isEmpty()) {
                        Text("Enter notes...", fontSize = 18.sp, color = placeholderColor)
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Save button
        Button(
            onClick = {
                if (text.isNotEmpty()) {
                    coroutineScope.launch {
                        notesViewModel.addNote(text)
                        text = "" // Clear the text field after saving
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor
            ),
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                "Save Note",
                color = textColor
            )
        }
    }
}