package com.example.todolist.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.domain.model.SortOrder
import com.example.todolist.domain.model.Task
import com.example.todolist.domain.repository.ScoreRepository
import com.example.todolist.domain.usecase.task.CompleteTaskUseCase
import com.example.todolist.domain.usecase.task.CreateTaskUseCase
import com.example.todolist.domain.usecase.task.DeleteTaskUseCase
import com.example.todolist.domain.usecase.task.GetTodayTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val scoreRepository: ScoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        _uiState.update { it.copy(isLoading = true) }
        
        val today = LocalDate.now()
        
        viewModelScope.launch {
            combine(
                getTodayTasksUseCase(),
                scoreRepository.observeRecordForDate(today)
            ) { todayTasks, scoreRecord ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        habitTasks = todayTasks.habitTasks,
                        dailyTasks = todayTasks.dailyTasks,
                        oneTimeTasks = todayTasks.oneTimeTasks,
                        todayScore = scoreRecord,
                        errorMessage = null
                    )
                }
            }.catch { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }.collect {}
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            try {
                val result = completeTaskUseCase(task)
                _uiState.update {
                    it.copy(
                        showPointsAnimation = true,
                        animationPoints = result.pointsGained,
                        animationStreak = if (task is Task.Habit) result.newStreak else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                val deleted = deleteTaskUseCase(task.id)
                if (deleted != null) {
                    _uiState.update {
                        it.copy(
                            deletedTask = deleted,
                            showUndoSnackbar = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun undoDelete() {
        val taskToRestore = _uiState.value.deletedTask ?: return
        viewModelScope.launch {
            try {
                createTaskUseCase(taskToRestore)
                _uiState.update {
                    it.copy(
                        deletedTask = null,
                        showUndoSnackbar = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun dismissAnimation() {
        _uiState.update {
            it.copy(
                showPointsAnimation = false,
                animationPoints = 0,
                animationStreak = null
            )
        }
    }

    fun filterByCategory(categoryId: String?) {
        _uiState.update {
            it.copy(selectedCategoryFilter = categoryId)
        }
    }

    fun setSortOrder(order: SortOrder) {
        _uiState.update {
            it.copy(sortOrder = order)
        }
    }

    fun dismissUndoSnackbar() {
        _uiState.update {
            it.copy(showUndoSnackbar = false)
        }
    }
}
