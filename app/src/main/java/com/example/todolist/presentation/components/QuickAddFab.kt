package com.example.todolist.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolist.domain.model.TaskType
import com.example.todolist.presentation.theme.ToDoListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddFab(
    onTypeSelected: (TaskType) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ExtendedFloatingActionButton(
        onClick = { isSheetOpen = true },
        icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Quick Add") },
        text = { Text("Tạo mới") },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    )

    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Bạn muốn thêm công việc nào?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                QuickAddOptionItem(
                    icon = Icons.Default.Refresh,
                    title = "Thói quen",
                    subtitle = "Lặp đi lặp lại hàng ngày hoặc định kỳ",
                    iconColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            isSheetOpen = false
                            onTypeSelected(TaskType.HABIT)
                        }
                    }
                )

                QuickAddOptionItem(
                    icon = Icons.Default.DateRange,
                    title = "Công việc hàng ngày",
                    subtitle = "Nhiệm vụ cần hoàn thành hôm nay",
                    iconColor = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            isSheetOpen = false
                            onTypeSelected(TaskType.DAILY)
                        }
                    }
                )

                QuickAddOptionItem(
                    icon = Icons.Default.Star,
                    title = "Có deadline cụ thể",
                    subtitle = "Công việc one-time có ngày giờ giới hạn",
                    iconColor = Color(0xFFF59E0B), // StreakAmber color
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            isSheetOpen = false
                            onTypeSelected(TaskType.ONE_TIME)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun QuickAddOptionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun QuickAddFabPreview() {
    ToDoListTheme {
        QuickAddFab(onTypeSelected = {})
    }
}
