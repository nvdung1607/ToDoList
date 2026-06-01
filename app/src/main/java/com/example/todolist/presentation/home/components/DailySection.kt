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
fun DailySection(
    tasks: List<Task.Daily>,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    // Sắp xếp đưa những task bị rollover (rollOverCount > 0) lên đầu
    val sortedTasks = tasks.sortedWith(
        compareByDescending<Task.Daily> { it.rollOverCount > 0 }
            .thenBy { it.isCompleted }
    )

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = "Công việc hôm nay",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        if (sortedTasks.isEmpty()) {
            EmptyState(
                title = "Hôm nay thảnh thơi",
                subtitle = "Không có công việc daily nào cần giải quyết."
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
