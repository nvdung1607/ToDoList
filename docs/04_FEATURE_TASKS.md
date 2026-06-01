# 04 — Feature Tasks (MVP To-Do List for AI/Dev)

Danh sách task được chia nhỏ để implement MVP theo thứ tự dependency.
Mỗi task độc lập, có thể giao cho AI hoặc developer thực hiện riêng lẻ.

**Quy ước trạng thái:** `[ ]` chưa làm · `[/]` đang làm · `[x]` hoàn thành

---

## Dependency Graph

```
Phase 0 (Setup)
    ↓
Phase 1 (Domain + Data)
    ↓
Phase 2 (Use Cases)
    ↓
Phase 3 (Notifications) ──┐
Phase 4 (Presentation)  ──┤  ← Song song được
    ↓
Phase 5 (Cloud Sync)
```

---

# PHASE 0 — Project Setup

## [ ] TASK-01 · Dependencies & Hilt Foundation

**Mục tiêu:** Cài đặt toàn bộ thư viện và setup Hilt DI.

**Files:**
- `app/build.gradle.kts` — thêm tất cả dependencies
- `build.gradle.kts` (root) — Hilt + KSP classpath
- `ToDoApplication.kt` — `@HiltAndroidApp`
- `di/DatabaseModule.kt` — `@Provides` AppDatabase + DAOs
- `di/RepositoryModule.kt` — `@Binds` interface → impl
- `di/NotificationModule.kt` — NotificationManager
- `core/common/Constants.kt` — hằng số toàn app

**Logic chính:**
```kotlin
// Constants.kt
const val DB_NAME = "todolist.db"
const val NOTIFICATION_CHANNEL_ID = "todolist_reminders"
const val HABIT_BASE_SCORE = 15
const val DAILY_BASE_SCORE = 10
const val ONE_TIME_BASE_SCORE = 20

// DatabaseModule.kt
@Provides @Singleton
fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
    Room.databaseBuilder(ctx, AppDatabase::class.java, DB_NAME).build()

@Provides fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
// ... tương tự cho CategoryDao, CompletionLogDao, ScoreDao
```

**Dependencies cần thêm:**
- Room + KSP processor
- Hilt + hilt-navigation-compose
- Compose BOM (Material3, Navigation)
- DataStore Preferences
- WorkManager + hilt-work
- Kotlin Coroutines + Flow
- Vico Charts
- Firebase BoM (Auth + Firestore)
- Google Credential Manager

---

## [ ] TASK-02 · Core Utilities & Extensions

**Mục tiêu:** Viết utility functions dùng chung để không lặp code.

**Files:**
- `core/common/Resource.kt`
- `core/utils/DateUtils.kt`
- `core/utils/ScoreCalculator.kt`
- `core/extensions/DateExtensions.kt`

**Logic chính:**
```kotlin
// Resource.kt
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String) : Resource<T>()
    class Loading<T> : Resource<T>()
}

// ScoreCalculator.kt — xem chi tiết trong 03_DATA_MODELS.md §5

// DateExtensions.kt
fun LocalDate.isToday(): Boolean = this == LocalDate.now()
fun LocalDate.isTomorrow(): Boolean = this == LocalDate.now().plusDays(1)
fun LocalDate.toFormattedString(): String  // "Hôm nay", "Ngày mai", "01/06"
```

---

# PHASE 1 — Domain & Data Infrastructure

## [ ] TASK-03 · Repository Interfaces + Implementations

**Mục tiêu:** Định nghĩa contract và implement offline-first repositories.

**Files:**
- `domain/repository/TaskRepository.kt` (interface)
- `domain/repository/CategoryRepository.kt` (interface)
- `domain/repository/ScoreRepository.kt` (interface)
- `data/repository/TaskRepositoryImpl.kt`
- `data/repository/CategoryRepositoryImpl.kt`
- `data/repository/ScoreRepositoryImpl.kt`

**Logic chính:**
```kotlin
// TaskRepository.kt (interface)
interface TaskRepository {
    fun observeHabitTasksForToday(dayOfWeek: Int): Flow<List<Task>>
    fun observeDailyTasksForDate(date: LocalDate): Flow<List<Task>>
    fun observeActiveOneTimeTasks(): Flow<List<Task>>
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun getTaskById(taskId: String): Task?
    suspend fun getUncompletedDailyBefore(date: LocalDate): List<Task>
    suspend fun resetHabitTasksCompletion()
    suspend fun getAllTasks(): List<Task>
}

// TaskRepositoryImpl — mỗi hàm: gọi DAO → map qua TaskMapper → trả về Domain model
```

---

## [ ] TASK-04 · UserPreferences DataStore

**Mục tiêu:** Lưu cài đặt người dùng (theme, notifications, sort order).

**Files:**
- `data/preferences/UserPreferences.kt`
- `data/preferences/UserPreferencesDataStore.kt`

**Logic chính:**
```kotlin
class UserPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateTheme(mode: ThemeMode)
    suspend fun updateHabitNotification(enabled: Boolean)
    suspend fun updateEveningReminder(enabled: Boolean, time: String)
    suspend fun updateSortOrder(order: SortOrder)
}
```

---

# PHASE 2 — Use Cases (Business Logic)

## [ ] TASK-05 · Task CRUD Use Cases

**Mục tiêu:** Encapsulate logic tạo/sửa/xóa/lấy task.

**Files:**
- `domain/usecase/task/CreateTaskUseCase.kt`
- `domain/usecase/task/UpdateTaskUseCase.kt`
- `domain/usecase/task/DeleteTaskUseCase.kt`
- `domain/usecase/task/GetTodayTasksUseCase.kt`
- `domain/usecase/task/GetTasksByDateRangeUseCase.kt`

**Logic chính:**
```kotlin
// GetTodayTasksUseCase — combine 3 Flows
operator fun invoke(date: LocalDate): Flow<TodayTasksResult> = combine(
    taskRepository.observeHabitTasksForToday(date.dayOfWeek.value),
    taskRepository.observeDailyTasksForDate(date),
    taskRepository.observeActiveOneTimeTasks()
) { habits, dailies, oneTimes ->
    TodayTasksResult(
        habitTasks = habits.filterIsInstance<Task.Habit>(),
        dailyTasks = dailies.filterIsInstance<Task.Daily>(),
        oneTimeTasks = oneTimes.filterIsInstance<Task.OneTime>()
    )
}

// CreateTaskUseCase
// 1. Validate title không rỗng
// 2. Gán UUID mới nếu id rỗng
// 3. taskRepository.createTask(task)
// 4. Schedule notification nếu cần
```

---

## [ ] TASK-06 · Complete Task + Score Use Cases ⭐ (Logic phức tạp nhất)

**Mục tiêu:** Xử lý flow tick hoàn thành: streak → điểm → log → ScoreRecord.

**Files:**
- `domain/usecase/task/CompleteTaskUseCase.kt`
- `domain/usecase/habit/UpdateStreakUseCase.kt`
- `domain/usecase/score/CalculateScoreUseCase.kt`
- `domain/usecase/score/GetTotalScoreUseCase.kt`

**Logic chính:**
```kotlin
// CompleteTaskUseCase — 5 bước atomic
suspend operator fun invoke(task: Task): CompleteTaskResult {
    // Step 1: Tính streak mới (chỉ Habit)
    val newStreak = if (task is Task.Habit) updateStreakUseCase(task) else 0

    // Step 2: Tính điểm (base * multiplier)
    val points = calculateScoreUseCase(task.taskType, newStreak)

    // Step 3: Update task (isCompleted=true, streak, completedAt...)
    taskRepository.updateTask(buildUpdatedTask(task, newStreak))

    // Step 4: Ghi CompletionLog
    completionLogRepository.insertLog(buildLog(task, points, newStreak))

    // Step 5: Upsert ScoreRecord hôm nay (+= points)
    scoreRepository.addPointsForToday(points)

    return CompleteTaskResult(pointsGained = points, newStreak = newStreak)
}

// UpdateStreakUseCase
suspend fun invoke(habit: Task.Habit): Int {
    val yesterday = LocalDate.now().minusDays(1)
    return if (habit.lastCompletedDate == yesterday) habit.currentStreak + 1 else 1
}
```

**Return value:**
```kotlin
data class CompleteTaskResult(val pointsGained: Int, val newStreak: Int)
```

---

## [ ] TASK-07 · Roll-over Use Case + WorkManager

**Mục tiêu:** Tự động đẩy Daily Task chưa xong sang ngày mai mỗi đêm.

**Files:**
- `domain/usecase/task/RollOverTasksUseCase.kt`
- `notification/DailyRollOverWorker.kt`

**Logic chính:**
```kotlin
// RollOverTasksUseCase
suspend operator fun invoke() {
    val today = LocalDate.now()
    // 1. Lấy Daily Task chưa hoàn thành trước hôm nay
    val overdue = taskRepository.getUncompletedDailyBefore(today)
    // 2. Update: scheduledDate = today, rollOverCount++
    overdue.forEach { task ->
        taskRepository.updateTask((task as Task.Daily).copy(
            scheduledDate = today,
            rollOverCount = task.rollOverCount + 1
        ))
    }
    // 3. Reset isCompleted = false cho tất cả Habit Task
    taskRepository.resetHabitTasksCompletion()
}

// DailyRollOverWorker — PeriodicWorkRequest mỗi 24h, initial delay đến 00:01 sáng hôm sau
```

---

## [ ] TASK-08 · Statistics Use Cases

**Mục tiêu:** Tổng hợp data cho Dashboard.

**Files:**
- `domain/usecase/stats/GetDailyStatsUseCase.kt`
- `domain/usecase/stats/GetHeatmapDataUseCase.kt`
- `domain/usecase/stats/GetHabitStreakStatsUseCase.kt`
- `domain/usecase/stats/GetMostSkippedTasksUseCase.kt`
- `domain/usecase/stats/GetCompletionHistoryUseCase.kt`

**Logic chính:**
```kotlin
// GetHeatmapDataUseCase — 3 tháng gần nhất
operator fun invoke(monthsBack: Int = 3): Flow<List<HeatmapEntry>> {
    val to = LocalDate.now()
    val from = to.minusMonths(monthsBack.toLong())
    return completionLogRepository.getActivityCountByDate(from, to)
        .map { it.map { item -> HeatmapEntry(LocalDate.parse(item.completed_date), item.count) } }
}

// GetHabitStreakStatsUseCase
// Map Habit tasks → HabitStreakStat, sort by currentStreak DESC

// GetMostSkippedTasksUseCase
// Query Daily tasks với rollOverCount > 0, sort DESC, take top 5
```

---

# PHASE 3 — Notifications

## [ ] TASK-09 · Notification Infrastructure

**Mục tiêu:** Notification channel, helper, scheduler, receiver.

**Files:**
- `notification/NotificationHelper.kt`
- `notification/NotificationScheduler.kt`
- `notification/TaskReminderReceiver.kt`
- `AndroidManifest.xml` (thêm receiver, permissions)

**Logic chính:**
```kotlin
// NotificationHelper.kt
fun createNotificationChannel(context: Context)
fun showTaskReminder(context: Context, taskId: String, title: String, message: String)
// Action "Hoàn thành" → PendingIntent → tap mở TaskDetail

// NotificationScheduler.kt
fun scheduleHabitReminder(taskId: String, title: String, time: LocalTime)
// AlarmManager.setRepeating() hàng ngày

fun scheduleOneTimeReminder(taskId: String, title: String, deadline: LocalDateTime, minutesBefore: Int)
// AlarmManager.setExactAndAllowWhileIdle() 1 lần

fun cancelReminder(taskId: String)

// AndroidManifest.xml — thêm:
// <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
// <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
// <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
// <receiver android:name=".notification.TaskReminderReceiver"/>
```

---

# PHASE 4 — Presentation (UI)

## [ ] TASK-10 · App Theme & Design System

**Mục tiêu:** Design system hoàn chỉnh với dark/light mode.

**Files:**
- `presentation/theme/Color.kt`
- `presentation/theme/Typography.kt`
- `presentation/theme/Shape.kt`
- `presentation/theme/Theme.kt`

**Palette:**
```kotlin
// Primary: Indigo/Violet (#6366F1 → #8B5CF6)
// Background Dark: #0F0F14
// Surface Dark: #1A1A24
// Accent/Streak: Amber (#F59E0B)
// Success: Emerald (#10B981)
// Error/Overdue: Rose (#F43F5E)
// Font: Inter hoặc Outfit (Google Fonts)
```

---

## [ ] TASK-11 · Navigation & Bottom Bar

**Mục tiêu:** Setup toàn bộ navigation routes.

**Files:**
- `presentation/navigation/Screen.kt`
- `presentation/navigation/AppNavHost.kt`
- `presentation/navigation/BottomNavBar.kt`
- `MainActivity.kt` (update)

**Routes:**
```kotlin
sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object Dashboard  : Screen("dashboard")
    object Categories : Screen("categories")
    object Settings   : Screen("settings")
    object AddTask    : Screen("add_task")
    data class TaskDetail(val taskId: String) : Screen("task/{taskId}")
}
// Deep link: "todolist://task/{taskId}" từ notification
```

---

## [ ] TASK-12 · Shared UI Components

**Mục tiêu:** Composable tái sử dụng toàn app.

**Files:**
- `presentation/components/TaskCard.kt`
- `presentation/components/ProgressRing.kt`
- `presentation/components/StreakBadge.kt`
- `presentation/components/PriorityChip.kt`
- `presentation/components/CategoryTag.kt`
- `presentation/components/EmptyState.kt`
- `presentation/components/ConfirmDialog.kt`
- `presentation/components/QuickAddFab.kt`

**TaskCard spec:**
- Swipe phải (xanh) → callback `onComplete`
- Swipe trái (đỏ) → callback `onDelete`
- `rollOverCount > 0` → badge vàng "Từ hôm qua ⚠️"
- `isCompleted = true` → text strikethrough + opacity 0.5
- Tap → callback `onClick`

---

## [ ] TASK-13 · Home Screen

**Mục tiêu:** Màn hình chính — 3 section task + header + FAB.

**Files:**
- `presentation/home/HomeUiState.kt`
- `presentation/home/HomeViewModel.kt`
- `presentation/home/HomeScreen.kt`
- `presentation/home/components/TodaySummaryHeader.kt`
- `presentation/home/components/HabitSection.kt`
- `presentation/home/components/DailySection.kt`
- `presentation/home/components/OneTimeSection.kt`

**HomeUiState:**
```kotlin
data class HomeUiState(
    val isLoading: Boolean = true,
    val habitTasks: List<Task.Habit> = emptyList(),
    val dailyTasks: List<Task.Daily> = emptyList(),
    val oneTimeTasks: List<Task.OneTime> = emptyList(),
    val todayScore: ScoreRecord? = null,
    val selectedCategoryFilter: String? = null,
    val sortOrder: SortOrder = SortOrder.PRIORITY,
    // Animations
    val showPointsAnimation: Boolean = false,
    val animationPoints: Int = 0,
    val animationStreak: Int? = null,
    // Undo delete
    val deletedTask: Task? = null,
    val showUndoSnackbar: Boolean = false,
    val errorMessage: String? = null
)
```

---

## [ ] TASK-14 · Add Task Screen

**Mục tiêu:** Bottom sheet tạo task — quick add + expanded mode.

**Files:**
- `presentation/task/add/AddTaskUiState.kt`
- `presentation/task/add/AddTaskViewModel.kt`
- `presentation/task/add/AddTaskScreen.kt`
- `presentation/task/add/components/TaskTypeSelector.kt`
- `presentation/task/add/components/RecurrencePicker.kt`
- `presentation/task/add/components/DeadlinePicker.kt`
- `presentation/task/add/components/PrioritySelector.kt`
- `presentation/task/detail/TaskDetailScreen.kt`
- `presentation/task/detail/TaskDetailViewModel.kt`

**AddTaskUiState:**
```kotlin
data class AddTaskUiState(
    val taskType: TaskType = TaskType.DAILY,
    val title: String = "",
    val note: String = "",
    val selectedCategoryId: String? = null,
    val priority: Priority = Priority.MEDIUM,
    // HABIT fields
    val recurrenceType: RecurrenceType = RecurrenceType.DAILY,
    val selectedDays: Set<Int> = emptySet(),
    val reminderTime: LocalTime? = null,
    val durationGoalMinutes: Int? = null,
    // DAILY fields
    val scheduledDate: LocalDate = LocalDate.now(),
    val deadlineTime: LocalTime? = null,
    // ONE_TIME fields
    val deadline: LocalDateTime? = null,
    val reminderMinutesBefore: Int? = null,
    // UI
    val isExpanded: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,   // trigger đóng bottom sheet
    val titleError: String? = null,
    val error: String? = null
)
```

---

## [ ] TASK-15 · Dashboard Screen

**Mục tiêu:** 6 widget thống kê với filter ngày/tuần/tháng.

**Files:**
- `presentation/dashboard/DashboardUiState.kt`
- `presentation/dashboard/DashboardViewModel.kt`
- `presentation/dashboard/DashboardScreen.kt`
- `presentation/dashboard/components/CompletionBarChart.kt`
- `presentation/dashboard/components/ActivityHeatmap.kt`
- `presentation/dashboard/components/StreakLeaderboard.kt`
- `presentation/dashboard/components/ScoreTrendChart.kt`
- `presentation/dashboard/components/SkippedTasksList.kt`
- `presentation/dashboard/components/HistoryList.kt`

**DashboardUiState:**
```kotlin
enum class DateRange { DAY, WEEK, MONTH, ALL_TIME }

data class DashboardUiState(
    val selectedRange: DateRange = DateRange.WEEK,
    val isLoading: Boolean = true,
    val dailyStats: List<DailyStats> = emptyList(),
    val todayCompletionRate: Float = 0f,
    val totalPointsInRange: Int = 0,
    val allTimePoints: Int = 0,
    val heatmapData: List<HeatmapEntry> = emptyList(),
    val habitStreakStats: List<HabitStreakStat> = emptyList(),
    val mostSkippedTasks: List<SkippedTaskStat> = emptyList(),
    val completionHistory: List<CompletionLog> = emptyList(),
    val historyFilter: DateRange = DateRange.DAY,
    val errorMessage: String? = null
)
```

---

## [ ] TASK-16 · Settings & Category Screens

**Mục tiêu:** Cài đặt theme/notification/account + quản lý danh mục.

**Files:**
- `presentation/settings/SettingsViewModel.kt`
- `presentation/settings/SettingsScreen.kt`
- `presentation/settings/components/ThemeSection.kt`
- `presentation/settings/components/NotificationSection.kt`
- `presentation/settings/components/AccountSection.kt`
- `presentation/category/CategoryViewModel.kt`
- `presentation/category/CategoryScreen.kt`
- `presentation/category/components/CategoryItem.kt`
- `presentation/category/components/ColorPickerDialog.kt`

**SettingsUiState:**
```kotlin
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val habitNotificationsEnabled: Boolean = true,
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "21:00",
    val isSignedIn: Boolean = false,
    val userEmail: String? = null,
    val syncStatus: SyncStatus = SyncStatus.NOT_SYNCED,
    val isSigningIn: Boolean = false
)

enum class SyncStatus { NOT_SYNCED, SYNCING, SYNCED, ERROR }
```

---

# PHASE 5 — Cloud Sync

## [ ] TASK-17 · Firebase Auth + Cloud Sync

**Mục tiêu:** Google Sign-In tùy chọn + merge local → Firestore.

**Files:**
- `data/remote/firebase/FirebaseAuthSource.kt`
- `data/remote/firebase/FirebaseTaskSource.kt`
- `data/remote/firebase/FirebaseCategorySource.kt`
- `data/remote/dto/TaskDto.kt`
- `data/remote/dto/CategoryDto.kt`
- `data/repository/SyncRepositoryImpl.kt`
- `domain/repository/SyncRepository.kt`
- `domain/usecase/sync/SignInWithGoogleUseCase.kt`
- `domain/usecase/sync/SyncToFirebaseUseCase.kt`
- `di/NetworkModule.kt`

**Logic chính:**
```kotlin
// SyncToFirebaseUseCase
suspend operator fun invoke(): Flow<SyncProgress> = flow {
    val uid = authSource.currentUserId ?: throw Exception("Not signed in")
    emit(SyncProgress.Uploading("tasks", 0))
    val tasks = taskRepository.getAllTasks()
    tasks.chunked(500).forEach { batch ->
        firebaseTaskSource.batchWrite(uid, batch.map { it.toDto() })
    }
    emit(SyncProgress.Uploading("categories", 0))
    // ... upload categories, logs, score_records
    emit(SyncProgress.Done)
}

// Firestore path: users/{userId}/tasks/{taskId}
```

---

## Tổng hợp

| Task | Phase | Ưu tiên | Phụ thuộc |
|---|---|---|---|
| TASK-01 | 0 | 🔴 Critical | — |
| TASK-02 | 0 | 🔴 Critical | TASK-01 |
| TASK-03 | 1 | 🔴 Critical | TASK-01, 02 |
| TASK-04 | 1 | 🟡 High | TASK-01 |
| TASK-05 | 2 | 🔴 Critical | TASK-03 |
| TASK-06 | 2 | 🔴 Critical | TASK-03, 05 |
| TASK-07 | 2 | 🔴 Critical | TASK-03, 05 |
| TASK-08 | 2 | 🟡 High | TASK-03, 06 |
| TASK-09 | 3 | 🟡 High | TASK-01, 05 |
| TASK-10 | 4 | 🔴 Critical | TASK-01 |
| TASK-11 | 4 | 🔴 Critical | TASK-10 |
| TASK-12 | 4 | 🔴 Critical | TASK-10, 11 |
| TASK-13 | 4 | 🔴 Critical | TASK-05, 06, 12 |
| TASK-14 | 4 | 🔴 Critical | TASK-05, 12 |
| TASK-15 | 4 | 🟡 High | TASK-08, 12 |
| TASK-16 | 4 | 🟡 High | TASK-04, 12 |
| TASK-17 | 5 | 🟢 Medium | TASK-01→16 |
