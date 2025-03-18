package com.opennotes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.opennotes.ui.theme.BackgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.opennotes.ui.theme.DarkBackgroundColor


@Composable
fun SettingsScreen(
    viewModel: NotesViewModel
) {
    // State to control the dropdown menu visibility

    val isDarkMode = viewModel.isDarkMode.value

    // Theme-aware colors
    val backgroundColor = if (isDarkMode) DarkBackgroundColor else BackgroundColor
    val textColor = if (isDarkMode) Color.White else Color.Black


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Settings",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 32.dp),
                color = textColor
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 16.dp)
            )

            // Dark Mode Toggle Button
            Button(
                onClick = { viewModel.toggleDarkMode() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) Color.White else Color.DarkGray
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    if (isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
                    color = if (isDarkMode) Color.DarkGray else Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            // Clear All Data Button
            Button(
                onClick = { viewModel.clearAllData() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkMode) Color.DarkGray else Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    "Clear All Data",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}