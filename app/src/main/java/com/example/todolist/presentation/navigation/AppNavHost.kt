package com.example.todolist.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

import com.example.todolist.presentation.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) }
        ) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.Dashboard.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) }
        ) {
            DashboardScreen(navController)
        }
        composable(
            route = Screen.Categories.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) }
        ) {
            CategoryScreen(navController)
        }
        composable(
            route = Screen.Settings.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) }
        ) {
            SettingsScreen(navController)
        }
        composable(
            route = Screen.AddTask.route,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(300)) }
        ) {
            AddTaskScreen(navController)
        }
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument(Screen.TaskDetail.ARG_TASK_ID) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "todolist://task/{taskId}" }),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(300)) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(300)) }
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString(Screen.TaskDetail.ARG_TASK_ID).orEmpty()
            TaskDetailScreen(taskId = taskId, navController = navController)
        }
    }
}

// Placeholder Screens to avoid compile issues

@Composable
fun DashboardScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Dashboard Screen", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun CategoryScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Category Screen", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Settings Screen", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun AddTaskScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Add Task Screen", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun TaskDetailScreen(taskId: String, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Task Detail Screen: $taskId", style = MaterialTheme.typography.titleLarge)
    }
}
