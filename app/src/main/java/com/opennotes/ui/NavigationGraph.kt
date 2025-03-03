package com.opennotes.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph(navController: NavHostController) {
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
            WelcomeScreen()
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
            QueryScreen()
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
            NoteListScreen()
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
        ) { SettingsScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("Welcome", "Query", "Notes", "Settings")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen,
                onClick = {
                    if (currentRoute != screen) {
                        navController.navigate(screen) {
                            // avoid duplicate destinations
                            launchSingleTop = true
                        }
                    }
                },
                label = { Text(screen) },
                alwaysShowLabel = true,
                icon = {} // add icons here if we want
            )
        }
    }
}