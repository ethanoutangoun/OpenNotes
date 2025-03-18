package com.opennotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.opennotes.ui.theme.BackgroundColor
import com.opennotes.ui.theme.PrimaryColor
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(notesViewModel: NotesViewModel) {
    var text by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val saveNote = {
        if (text.isNotEmpty() && !isLoading) {
            isLoading = true
            keyboardController?.hide()
            coroutineScope.launch {
                notesViewModel.addNote(text)
                text = ""
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Open Notes ✏️",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Borderless TextField with placeholder and keyboard actions
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .padding(8.dp)
                .width(280.dp)
                .height(120.dp)
                .focusRequester(focusRequester),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    saveNote()
                }
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(8.dp)
                ) {
                    if (text.isEmpty()) {
                        Text("Enter notes...", fontSize = 18.sp, color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .height(36.dp)
                .width(120.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = PrimaryColor
                )
            } else {
                // Save button
                Button(
                    onClick = { saveNote() },
                    enabled = text.isNotEmpty(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Save Note")
                }
            }
        }
    }
}