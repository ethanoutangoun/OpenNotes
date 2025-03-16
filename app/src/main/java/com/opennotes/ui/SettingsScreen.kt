package com.opennotes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import com.opennotes.ui.theme.BackgroundColor

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor), // Set the background color to white
        contentAlignment = Alignment.Center
    ) {
        Text("Settings", fontSize = 24.sp)
    }
}