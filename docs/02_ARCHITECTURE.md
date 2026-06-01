# 02 — Architecture & Project Structure

**Pattern:** Clean Architecture + MVVM (Presentation Layer)
**Language:** Kotlin
**UI:** Jetpack Compose + Material3
**Min SDK:** 26 | **Target SDK:** 35

---

## 1. Layered Architecture

```
┌──────────────────────────────────────────┐
│          Presentation Layer              │
│   Compose UI  ←→  ViewModel  ←→  State  │
├──────────────────────────────────────────┤
│             Domain Layer                 │
│     Use Cases  +  Models  (pure Kotlin)  │
├──────────────────────────────────────────┤
│              Data Layer                  │
│   Room DB  |  Firebase  |  DataStore     │
└──────────────────────────────────────────┘
```

**Dependency Rule:** Domain không phụ thuộc vào Data hay Presentation.
Data và Presentation đều phụ thuộc vào Domain (Dependency Inversion).

### Data Flow
```
[Compose UI]
    ↕  collectAsStateWithLifecycle
[ViewModel]  →  UiState (StateFlow)  →  UI re-render
    ↕  inject (Hilt)
[Use Case]   →  Business logic thuần, không biết Room/Firebase
    ↕  inject (interface)
[Repository Impl]
    ├── Room DAO  (local — source of truth)
    └── Firebase  (sync khi đăng nhập)
```

---

## 2. Project Directory Structure

```
com.example.todolist/
│
├── app/
│   ├── ToDoApplication.kt            # @HiltAndroidApp, khởi tạo WorkManager
│   └── MainActivity.kt               # Entry point, host NavHost, observe theme
│
├── core/
│   ├── common/
│   │   ├── Resource.kt               # sealed class: Loading / Success / Error
│   │   └── Constants.kt              # DB_NAME, CHANNEL_ID, SCORE values, STREAK_MULTIPLIERS
│   ├── extensions/
│   │   ├── DateExtensions.kt         # isToday(), isTomorrow(), toFormattedString()
│   │   └── FlowExtensions.kt         # collectAsStateWithLifecycle helpers
│   └── utils/
│       ├── DateUtils.kt              # getRollOverDate(), getWeekRange(), getMonthRange()
│       └── ScoreCalculator.kt        # calculateScore(taskType, streak) → Int
│
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt        # @Database, version=1, exportSchema=true
│   │   │   └── Migrations.kt         # Schema migrations (version bump)
│   │   ├── entity/
│   │   │   ├── TaskEntity.kt         # Single Table Inheritance — 3 loại task, 1 bảng
│   │   │   ├── CategoryEntity.kt
│   │   │   ├── CompletionLogEntity.kt
│   │   │   └── ScoreRecordEntity.kt
│   │   └── dao/
│   │       ├── TaskDao.kt
│   │       ├── CategoryDao.kt
│   │       ├── CompletionLogDao.kt
│   │       └── ScoreDao.kt
│   ├── remote/
│   │   ├── firebase/
│   │   │   ├── FirebaseAuthSource.kt  # Google Sign-In wrapper
│   │   │   ├── FirebaseTaskSource.kt  # Firestore CRUD tasks
│   │   │   └── FirebaseCategorySource.kt
│   │   └── dto/
│   │       ├── TaskDto.kt             # Firestore document shape
│   │       └── CategoryDto.kt
│   ├── mapper/
│   │   ├── TaskMapper.kt             # TaskEntity ↔ Task ↔ TaskDto
│   │   └── EntityMappers.kt          # Category, CompletionLog, ScoreRecord mappers
│   ├── repository/
│   │   ├── TaskRepositoryImpl.kt
│   │   ├── CategoryRepositoryImpl.kt
│   │   ├── ScoreRepositoryImpl.kt
│   │   └── SyncRepositoryImpl.kt     # Merge local → Firebase
│   └── preferences/
│       ├── UserPreferences.kt        # data class + enums (ThemeMode, SortOrder)
│       └── UserPreferencesDataStore.kt
│
├── domain/
│   ├── model/
│   │   ├── Task.kt                   # sealed class: Habit | Daily | OneTime
│   │   ├── TaskType.kt               # enum: HABIT / DAILY / ONE_TIME
│   │   ├── Priority.kt               # enum: HIGH / MEDIUM / LOW
│   │   ├── RecurrenceConfig.kt       # data class + enum RecurrenceType
│   │   ├── Category.kt
│   │   ├── ScoreRecord.kt
│   │   ├── CompletionLog.kt
│   │   └── DashboardStats.kt        # DailyStats, HeatmapEntry, HabitStreakStat, SkippedTaskStat
│   ├── repository/
│   │   ├── TaskRepository.kt         # interface
│   │   ├── CategoryRepository.kt
│   │   ├── ScoreRepository.kt
│   │   └── SyncRepository.kt
│   └── usecase/
│       ├── task/
│       │   ├── GetTodayTasksUseCase.kt
│       │   ├── CreateTaskUseCase.kt
│       │   ├── UpdateTaskUseCase.kt
│       │   ├── CompleteTaskUseCase.kt    # orchestrates: streak + score + log + record
│       │   ├── DeleteTaskUseCase.kt
│       │   ├── RollOverTasksUseCase.kt
│       │   └── GetTasksByDateRangeUseCase.kt
│       ├── habit/
│       │   ├── UpdateStreakUseCase.kt
│       │   └── ScheduleHabitNotificationUseCase.kt
│       ├── score/
│       │   ├── CalculateScoreUseCase.kt
│       │   └── GetTotalScoreUseCase.kt
│       ├── stats/
│       │   ├── GetDailyStatsUseCase.kt
│       │   ├── GetHeatmapDataUseCase.kt
│       │   ├── GetHabitStreakStatsUseCase.kt
│       │   ├── GetMostSkippedTasksUseCase.kt
│       │   └── GetCompletionHistoryUseCase.kt
│       └── sync/
│           ├── SignInWithGoogleUseCase.kt
│           └── SyncToFirebaseUseCase.kt
│
├── presentation/
│   ├── navigation/
│   │   ├── Screen.kt                 # sealed class — tất cả routes
│   │   ├── AppNavHost.kt             # NavHost + deep link từ notification
│   │   └── BottomNavBar.kt           # 4 tabs: Home / Stats / Categories / Settings
│   ├── theme/
│   │   ├── Color.kt                  # Dark + Light palette (Indigo/Violet primary)
│   │   ├── Typography.kt             # Inter / Outfit từ Google Fonts
│   │   ├── Shape.kt                  # Bo góc components
│   │   └── Theme.kt                  # ToDoListTheme wrapper, ThemeMode logic
│   ├── components/                   # Shared Composables
│   │   ├── TaskCard.kt               # Swipe-to-complete/delete, roll-over badge
│   │   ├── ProgressRing.kt           # Canvas circular progress
│   │   ├── StreakBadge.kt            # "🔥 {n}" với gradient animation
│   │   ├── PriorityChip.kt
│   │   ├── CategoryTag.kt
│   │   ├── EmptyState.kt
│   │   ├── ConfirmDialog.kt
│   │   └── QuickAddFab.kt            # FAB + BottomSheet type selector
│   ├── home/
│   │   ├── HomeUiState.kt
│   │   ├── HomeViewModel.kt
│   │   ├── HomeScreen.kt
│   │   └── components/
│   │       ├── TodaySummaryHeader.kt
│   │       ├── HabitSection.kt
│   │       ├── DailySection.kt
│   │       └── OneTimeSection.kt
│   ├── task/
│   │   ├── add/
│   │   │   ├── AddTaskUiState.kt
│   │   │   ├── AddTaskViewModel.kt
│   │   │   ├── AddTaskScreen.kt
│   │   │   └── components/
│   │   │       ├── TaskTypeSelector.kt
│   │   │       ├── RecurrencePicker.kt
│   │   │       ├── DeadlinePicker.kt
│   │   │       └── PrioritySelector.kt
│   │   └── detail/
│   │       ├── TaskDetailViewModel.kt
│   │       └── TaskDetailScreen.kt
│   ├── dashboard/
│   │   ├── DashboardUiState.kt
│   │   ├── DashboardViewModel.kt
│   │   ├── DashboardScreen.kt
│   │   └── components/
│   │       ├── CompletionBarChart.kt
│   │       ├── ActivityHeatmap.kt
│   │       ├── StreakLeaderboard.kt
│   │       ├── ScoreTrendChart.kt
│   │       ├── SkippedTasksList.kt
│   │       └── HistoryList.kt
│   ├── category/
│   │   ├── CategoryViewModel.kt
│   │   ├── CategoryScreen.kt
│   │   └── components/
│   │       ├── CategoryItem.kt
│   │       └── ColorPickerDialog.kt
│   └── settings/
│       ├── SettingsViewModel.kt
│       ├── SettingsScreen.kt
│       └── components/
│           ├── ThemeSection.kt
│           ├── NotificationSection.kt
│           └── AccountSection.kt
│
├── notification/
│   ├── NotificationHelper.kt         # createChannel(), showTaskReminder()
│   ├── NotificationScheduler.kt      # AlarmManager schedule/cancel
│   ├── TaskReminderReceiver.kt        # BroadcastReceiver → trigger notification
│   └── DailyRollOverWorker.kt        # WorkManager PeriodicWork lúc 00:01
│
└── di/
    ├── DatabaseModule.kt             # @Provides AppDatabase + DAOs
    ├── RepositoryModule.kt           # @Binds interface → impl
    ├── NetworkModule.kt              # Firebase instances
    └── NotificationModule.kt         # NotificationManager, Scheduler
```

---

## 3. Tech Stack

| Layer | Library | Version |
|---|---|---|
| **UI** | Jetpack Compose | BOM 2024.x |
| **UI** | Material3 | via Compose BOM |
| **Navigation** | Navigation Compose | 2.7.x |
| **ViewModel** | lifecycle-viewmodel-compose | 2.7.x |
| **DI** | Hilt | 2.51+ |
| **Local DB** | Room | 2.6+ |
| **Preferences** | DataStore Preferences | 1.1.x |
| **Cloud DB** | Firebase Firestore | via BoM |
| **Auth** | Firebase Auth + Credential Manager | via BoM |
| **Async** | Kotlin Coroutines + Flow | 1.8.x |
| **Background** | WorkManager | 2.9.x |
| **Charts** | Vico (patrykandpatrick) | latest |
| **Build** | KSP (for Room + Hilt) | latest |

---

## 4. Naming Conventions

| Loại | Quy ước | Ví dụ |
|---|---|---|
| Room Entity | `XxxEntity` | `TaskEntity` |
| Domain Model | Tên thuần | `Task`, `Category` |
| Firebase DTO | `XxxDto` | `TaskDto` |
| Repository Interface | `XxxRepository` | `TaskRepository` |
| Repository Impl | `XxxRepositoryImpl` | `TaskRepositoryImpl` |
| Use Case | `VerbNounUseCase` | `CompleteTaskUseCase` |
| ViewModel | `XxxViewModel` | `HomeViewModel` |
| UI State | `XxxUiState` | `HomeUiState` |
| Composable Screen | `XxxScreen` | `HomeScreen` |
| Composable Component | Noun/Adj | `TaskCard`, `StreakBadge` |

---

## 5. Key Design Decisions

| Quyết định | Lý do |
|---|---|
| **Single Table Inheritance** cho Task | Đơn giản hóa query "task hôm nay" — không cần JOIN |
| **Sealed class Task** trong Domain | Compiler enforce exhaustive `when`, type-safe |
| **Date lưu dạng String** ("yyyy-MM-dd") | WHERE clause SQL trực tiếp, không cần TypeConverter |
| **CompletionLog denormalized** `completed_date` | GROUP BY ngày cho heatmap không cần `strftime()` phức tạp |
| **Offline-first** (Room là source of truth) | App hoạt động không cần internet |
| **Firebase sync tùy chọn** | Không ép user đăng nhập — giảm friction |

---

## 6. Database Schema

```
categories (id PK, name UNIQUE, color_hex, created_at)
      │ FK SET NULL
      ▼
tasks (id PK, task_type, title, note, category_id FK,
       priority, is_completed, created_at,
       -- HABIT: recurrence_type, recurrence_days, reminder_time,
                 current_streak, longest_streak, duration_goal_minutes, last_completed_date
       -- DAILY: scheduled_date [INDEX], deadline_time, roll_over_count, original_date
       -- ONE_TIME: deadline_date_time, reminder_minutes_before, completed_at)
      │ FK CASCADE
      ▼
completion_logs (id PK, task_id FK, task_title, task_type,
                 completed_at, completed_date [INDEX], points_gained, streak_at_completion)

score_records (id PK, date [UNIQUE INDEX], points_earned,
               tasks_completed, tasks_total)
```

---

## 7. Firestore Structure (Cloud Sync)

```
users/
  {userId}/
    tasks/
      {taskId} → TaskDto fields
    categories/
      {categoryId} → CategoryDto fields
    completion_logs/
      {logId} → CompletionLogDto fields
    score_records/
      {date} → ScoreRecordDto fields
```
