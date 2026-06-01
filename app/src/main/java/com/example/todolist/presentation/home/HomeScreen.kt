package com.example.todolist.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.todolist.presentation.components.QuickAddFab
import com.example.todolist.presentation.home.components.DailySection
import com.example.todolist.presentation.home.components.HabitSection
import com.example.todolist.presentation.home.components.OneTimeSection
import com.example.todolist.presentation.home.components.TodaySummaryHeader
import com.example.todolist.presentation.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Xử lý thông báo Snackbar Undo khi xóa task thành công
    LaunchedEffect(uiState.showUndoSnackbar) {
        if (uiState.showUndoSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = "Đã xóa công việc",
                actionLabel = "Hoàn tác"
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.dismissUndoSnackbar()
            }
        }
    }

    // Tự động ẩn hiệu ứng điểm sau 1.5 giây
    LaunchedEffect(uiState.showPointsAnimation) {
        if (uiState.showPointsAnimation) {
            kotlinx.coroutines.delay(1500)
            viewModel.dismissAnimation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hôm nay",
                            style = MaterialTheme.typography.titleLarge
                        )
                        val formattedDate = remember {
                            LocalDate.now().format(
                                DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("vi"))
                            )
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Mở bộ lọc nhanh */ }) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Lọc")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            QuickAddFab(
                onTypeSelected = { taskType ->
                    // Navigate sang màn hình AddTask kèm loại task type
                    navController.navigate("add_task?type=${taskType.name}")
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        TodaySummaryHeader(
                            score = uiState.todayScore,
                            habitCount = uiState.habitTasks.size,
                            dailyCount = uiState.dailyTasks.size,
                            oneTimeCount = uiState.oneTimeTasks.size
                        )
                    }

                    item {
                        HabitSection(
                            tasks = uiState.habitTasks,
                            onComplete = { viewModel.completeTask(it) },
                            onDelete = { viewModel.deleteTask(it) },
                            onClick = { navController.navigate(Screen.TaskDetail.createRoute(it.id)) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        DailySection(
                            tasks = uiState.dailyTasks,
                            onComplete = { viewModel.completeTask(it) },
                            onDelete = { viewModel.deleteTask(it) },
                            onClick = { navController.navigate(Screen.TaskDetail.createRoute(it.id)) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        OneTimeSection(
                            tasks = uiState.oneTimeTasks,
                            onComplete = { viewModel.completeTask(it) },
                            onDelete = { viewModel.deleteTask(it) },
                            onClick = { navController.navigate(Screen.TaskDetail.createRoute(it.id)) }
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // Hiệu ứng cộng điểm mượt mà "+X ⭐" ở giữa góc trên màn hình
            AnimatedVisibility(
                visible = uiState.showPointsAnimation,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                val streakText = if (uiState.animationStreak != null && uiState.animationStreak!! > 0) {
                    " 🔥 ${uiState.animationStreak}"
                } else ""
                
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "+${uiState.animationPoints} ⭐$streakText",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
