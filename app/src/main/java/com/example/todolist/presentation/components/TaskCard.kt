package com.example.todolist.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.Priority
import com.example.todolist.domain.model.Task
import com.example.todolist.presentation.theme.ToDoListTheme
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCard(
    task: Task,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var isDismissed by remember { mutableStateOf(false) }
    var actionToTrigger by remember { mutableStateOf<SwipeToDismissBoxValue?>(null) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    actionToTrigger = value
                    isDismissed = true
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    actionToTrigger = value
                    isDismissed = true
                    true
                }
                else -> false
            }
        }
    )

    LaunchedEffect(isDismissed) {
        if (isDismissed) {
            kotlinx.coroutines.delay(300) // Delay to let exit animation play out nicely
            when (actionToTrigger) {
                SwipeToDismissBoxValue.StartToEnd -> onComplete(task)
                SwipeToDismissBoxValue.EndToStart -> onDelete(task)
                else -> {}
            }
        }
    }

    AnimatedVisibility(
        visible = !isDismissed,
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(animationSpec = tween(300))
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                val color = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF10B981) // Emerald check
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFF43F5E) // Rose delete
                    else -> Color.Transparent
                }
                
                val alignment = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    else -> Alignment.CenterEnd
                }

                val icon = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Check
                    else -> Icons.Default.Delete
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color, shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = 24.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = modifier.padding(vertical = 4.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(task) },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .alpha(if (task.isCompleted) 0.4f else 1.0f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            // Badge check logic for specific Task variations
                            if (task is Task.Daily && task.rollOverCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "📅 +${task.rollOverCount} ngày",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFD97706),
                                    modifier = Modifier
                                        .background(Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (task is Task.OneTime && !task.isCompleted && task.deadline.isBefore(LocalDateTime.now())) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Quá hạn 🔴",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFF43F5E),
                                    modifier = Modifier
                                        .background(Color(0xFFFFE4E6), shape = RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (!task.note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = task.note.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PriorityChip(priority = task.priority)
                            
                            // A category tag representation can be displayed here. We will just pass null or default.
                            // In real usages this will be populated dynamically.
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TaskCardPreview() {
    ToDoListTheme {
        TaskCard(
            task = Task.Daily(
                id = "1",
                title = "Học lập trình Jetpack Compose",
                note = "Hoàn thành phần Custom Layout và Canvas",
                rollOverCount = 2,
                priority = Priority.HIGH,
                isCompleted = false
            ),
            onComplete = {},
            onDelete = {},
            onClick = {}
        )
    }
}
