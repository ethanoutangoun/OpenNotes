package com.opennotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.opennotes.ui.theme.BackgroundColor
import com.opennotes.ui.theme.DarkBackgroundColor
import com.opennotes.ui.theme.OpenNotesTheme

@Composable
fun NoteApp(viewModel: NotesViewModel) {
    val navController = rememberNavController()
    val backgroundColor = if (viewModel.isDarkMode.value) DarkBackgroundColor else BackgroundColor


    Scaffold(
        bottomBar = { BottomNavigationBar(navController, viewModel) },
        modifier = Modifier.background(backgroundColor)
    ) { padding ->

        Box(modifier = Modifier.padding(padding).background(backgroundColor)) {
            NavigationGraph(navController, viewModel)
        }
    }
}
