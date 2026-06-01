package com.example.todolist.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.Task
import com.example.todolist.presentation.components.EmptyState
import com.example.todolist.presentation.components.TaskCard

@Composable
fun OneTimeSection(
    tasks: List<Task.OneTime>,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sắp xếp các task có deadline theo trình tự thời gian tăng dần
    val sortedTasks = tasks.sortedWith(
        compareBy<Task.OneTime> { it.isCompleted }
            .thenBy { it.deadline }
    )

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = "Việc có deadline cụ thể",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        if (sortedTasks.isEmpty()) {
            EmptyState(
                title = "Không có deadline gần đây",
                subtitle = "Tạo các mục tiêu có mốc thời gian để làm việc năng suất hơn."
            )
        } else {
            sortedTasks.forEach { task ->
                TaskCard(
                    task = task,
                    onComplete = onComplete,
                    onDelete = onDelete,
                    onClick = onClick
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
