package com.example.todolist.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.Category
import com.example.todolist.presentation.theme.ToDoListTheme

@Composable
fun CategoryTag(
    category: Category?,
    modifier: Modifier = Modifier
) {
    if (category == null) return

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(category.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.secondary
    }

    Row(
        modifier = modifier
            .background(parsedColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(parsedColor, shape = CircleShape)
        )
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            color = parsedColor,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryTagPreview() {
    ToDoListTheme {
        CategoryTag(
            category = Category(
                id = "1",
                name = "Học tập",
                colorHex = "#8B5CF6"
            )
        )
    }
}
