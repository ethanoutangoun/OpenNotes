package com.opennotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import com.opennotes.ui.theme.BackgroundColor
import com.opennotes.ui.theme.OpenNotesTheme

@Composable
fun WelcomeScreen() {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome ✏️ to Open Notes!",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Borderless TextField with placeholder
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.padding(8.dp),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text("Enter notes...", fontSize = 18.sp, color = Color.Gray)
                }
                innerTextField()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    OpenNotesTheme {
        WelcomeScreen()
    }
}