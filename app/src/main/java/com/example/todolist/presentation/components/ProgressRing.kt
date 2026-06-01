package com.example.todolist.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.presentation.theme.ToDoListTheme

@Composable
fun ProgressRing(
    progress: Float,
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "progressAnimation"
    )

    // Màu sắc động tương ứng: đỏ < 0.5, vàng 0.5 - 0.79, xanh >= 0.8
    val progressColor = when {
        animatedProgress < 0.5f -> Color(0xFFF43F5E) // Red
        animatedProgress < 0.8f -> Color(0xFFF59E0B) // Amber/Yellow
        else -> Color(0xFF10B981) // Emerald/Green
    }

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(80.dp)) {
            // Background track ring
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.2f),
                style = Stroke(width = 6.dp.toPx())
            )

            // Primary progress arc
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$completedCount/$totalCount",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressRingPreview() {
    ToDoListTheme {
        ProgressRing(progress = 0.65f, completedCount = 13, totalCount = 20)
    }
}
