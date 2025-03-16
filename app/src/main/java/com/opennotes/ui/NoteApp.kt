package com.opennotes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.opennotes.ui.theme.OpenNotesTheme

@Composable
fun NoteApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavigationGraph(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteApp() {
    OpenNotesTheme { // Ensure the theme is applied in preview
        NoteApp()
    }
}