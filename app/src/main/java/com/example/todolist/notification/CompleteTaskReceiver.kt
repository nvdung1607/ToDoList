package com.example.todolist.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todolist.domain.repository.TaskRepository
import com.example.todolist.domain.usecase.task.CompleteTaskUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * BroadcastReceiver triggered when the user clicks "Hoàn thành" directly from notifications.
 */
@AndroidEntryPoint
class CompleteTaskReceiver : BroadcastReceiver() {

    @Inject
    lateinit var completeTaskUseCase: CompleteTaskUseCase

    @Inject
    lateinit var taskRepository: TaskRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "COMPLETE_TASK") {
            val taskId = intent.getStringExtra("task_id") ?: return
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val task = taskRepository.getTaskById(taskId)
                    if (task != null) {
                        completeTaskUseCase(task)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
