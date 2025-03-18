package com.opennotes.ui

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import com.opennotes.ui.theme.BlueSelected
import com.opennotes.ui.theme.GrayBackground
import com.opennotes.ui.theme.BlackUnselected
import com.opennotes.ui.theme.OpenNotesTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.opennotes.ui.theme.BackgroundColor
import com.opennotes.ui.theme.BorderColor
import androidx.lifecycle.viewmodel.compose.viewModel
import com.opennotes.ui.theme.DarkBackgroundColor
import com.opennotes.ui.theme.DarkBorderColor

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(navController: NavHostController, notesViewModel: NotesViewModel) {
    val screenOrder = listOf("Welcome", "Query", "Notes", "Settings")


    AnimatedNavHost(
        navController = navController,
        startDestination = "Welcome",
    ) {
        composable(
            route = "Welcome",
            enterTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                }
            },
            exitTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            }
        ) {
            WelcomeScreen(notesViewModel = notesViewModel)
        }

        composable(
            route = "Query",
            enterTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                }
            },
            exitTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            }
        ) {
            QueryScreen(viewModel = notesViewModel)
        }

        composable(
            route = "Notes",
            enterTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                }
            },
            exitTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            }
        ) {
            NoteListScreen(viewModel = notesViewModel)
        }

        composable(
            route = "Settings",
            enterTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                } else {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                }
            },
            exitTransition = {
                val isForward = screenOrder.indexOf(initialState.destination.route ?: "") <
                        screenOrder.indexOf(targetState.destination.route ?: "")
                if (isForward) {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                } else {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            }
        ) {
            SettingsScreen(notesViewModel)
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController, viewModel: NotesViewModel) {
    val items = listOf("Welcome", "Query", "Notes", "Settings")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = if (viewModel.isDarkMode.value) DarkBackgroundColor else BackgroundColor,
        modifier = Modifier.border(
            BorderStroke(1.dp, if (viewModel.isDarkMode.value) DarkBorderColor else BorderColor)
        )
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen
            val unselected = if (viewModel.isDarkMode.value) Color.LightGray else BlackUnselected
            val itemColor = if (isSelected) BlueSelected else unselected

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != screen) {
                        navController.navigate(screen) {
                            launchSingleTop = true
                        }
                    }
                },
                label = {
                    Text(
                        text = screen,
                        color = itemColor
                    )
                },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            "Welcome" -> Icons.Filled.Edit
                            "Query" -> Icons.Filled.Chat
                            "Notes" -> Icons.Filled.List
                            "Settings" -> Icons.Filled.Settings
                            else -> Icons.Filled.Home
                        },
                        contentDescription = null,
                        tint = itemColor
                    )
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = itemColor,
                    unselectedIconColor = itemColor,
                    selectedTextColor = itemColor,
                    unselectedTextColor = itemColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

