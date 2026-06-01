package com.example.todolist.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object Dashboard  : Screen("dashboard")
    object Categories : Screen("categories")
    object Settings   : Screen("settings")
    object AddTask    : Screen("add_task")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
        const val ARG_TASK_ID = "taskId"
    }
}

// Bottom nav items
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        label = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    BottomNavItem(
        screen = Screen.Dashboard,
        label = "Dashboard",
        icon = Icons.Outlined.Info, // custom icon mapping using basic Icons bundle to avoid missing dependency errors
        selectedIcon = Icons.Filled.Info
    ),
    BottomNavItem(
        screen = Screen.Categories,
        label = "Categories",
        icon = Icons.Outlined.List,
        selectedIcon = Icons.Filled.List
    ),
    BottomNavItem(
        screen = Screen.Settings,
        label = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings
    )
)
