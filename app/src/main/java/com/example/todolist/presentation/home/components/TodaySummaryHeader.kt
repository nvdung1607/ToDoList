package com.example.todolist.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.ScoreRecord
import com.example.todolist.presentation.components.ProgressRing
import com.example.todolist.presentation.theme.ToDoListTheme

@Composable
fun TodaySummaryHeader(
    score: ScoreRecord?,
    habitCount: Int,
    dailyCount: Int,
    oneTimeCount: Int,
    modifier: Modifier = Modifier
) {
    val totalTasks = score?.tasksTotal ?: (habitCount + dailyCount + oneTimeCount)
    val completedCount = score?.tasksCompleted ?: 0

    val progress = if (totalTasks > 0) completedCount.toFloat() / totalTasks else 0.0f
    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tiến độ hôm nay",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (totalTasks > 0) {
                        "Đã xong: $completedCount/$totalTasks ($percentage%)"
                    } else {
                        "Không có công việc nào hôm nay"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Điểm số: ${score?.pointsEarned ?: 0} ⭐",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            ProgressRing(
                progress = progress,
                completedCount = completedCount,
                totalCount = totalTasks
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodaySummaryHeaderPreview() {
    ToDoListTheme {
        TodaySummaryHeader(
            score = ScoreRecord(
                id = "1",
                date = java.time.LocalDate.now(),
                pointsEarned = 45,
                tasksCompleted = 3,
                tasksTotal = 6
            ),
            habitCount = 3,
            dailyCount = 2,
            oneTimeCount = 1
        )
    }
}
