package com.opennotes.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.opennotes.R
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
    val iconTint = if (isDarkMode) Color.LightGray else Color.DarkGray
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
            .background(backgroundColor)
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
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDarkMode) Color.DarkGray else MaterialTheme.colorScheme.primary,
                contentColor = if (isDarkMode) Color.White else Color.White,
                disabledContainerColor = if (isDarkMode) Color(0xFF1A1A1A) else Color.LightGray,
                disabledContentColor = if (isDarkMode) Color.Gray else Color.DarkGray
            ),
            enabled = queryInput.isNotEmpty() && !isSearching
        ) {
            Text(text = "Submit")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Show loading indicator while searching
        if (isSearching) {
            LoadingIcon()
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
            }
        }
    }
}


@Composable
fun LoadingIcon() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val infiniteTransition = rememberInfiniteTransition()

        val yOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 800,  // Reduced from 2000ms to 800ms (2.5x faster)
                    easing = CubicBezierEasing(0.2f, 0.0f, 0.1f, 1.0f) // Steeper curve for faster acceleration
                )
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.robot_empty_state),
                contentDescription = "Robot Empty State",
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = yOffset.dp)
                    .graphicsLayer {
                        rotationZ = rotation
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    }
            )
        }
    }
}