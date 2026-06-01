package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.todolist.domain.model.ThemeMode
import com.example.todolist.presentation.theme.ToDoListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // ThemeMode will be driven by UserPreferences / ViewModel later
            ToDoListTheme(themeMode = ThemeMode.SYSTEM) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation host will replace this placeholder
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "ToDoList",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    ToDoListTheme(themeMode = ThemeMode.LIGHT) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Text(
                text = "ToDoList Preview",
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}