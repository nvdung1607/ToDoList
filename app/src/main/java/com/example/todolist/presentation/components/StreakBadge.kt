package com.example.todolist.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.presentation.theme.ToDoListTheme

@Composable
fun StreakBadge(
    streak: Int,
    modifier: Modifier = Modifier
) {
    if (streak == 0) return

    var triggerAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(streak) {
        triggerAnimation = true
    }

    val scale by animateFloatAsState(
        targetValue = if (triggerAnimation) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            triggerAnimation = false
        },
        label = "streakScale"
    )

    // Gradient background Amber/Orange
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF59E0B), // StreakAmber
            Color(0xFFD97706)
        )
    )

    Box(
        modifier = modifier
            .scale(scale)
            .background(gradient, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔥 $streak",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StreakBadgePreview() {
    ToDoListTheme {
        StreakBadge(streak = 5)
    }
}
