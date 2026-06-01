package com.example.todolist.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.Priority
import com.example.todolist.presentation.theme.ToDoListTheme

@Composable
fun PriorityChip(
    priority: Priority,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (priority) {
        Priority.HIGH -> {
            // Đỏ
            Color(0xFFFFE4E6) to Color(0xFFF43F5E)
        }
        Priority.MEDIUM -> {
            // Vàng
            Color(0xFFFEF3C7) to Color(0xFFD97706)
        }
        Priority.LOW -> {
            // Xanh lá
            Color(0xFFD1FAE5) to Color(0xFF10B981)
        }
    }

    Text(
        text = priority.label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PriorityChipPreview() {
    ToDoListTheme {
        PriorityChip(priority = Priority.HIGH)
    }
}
