# 05 — Prompts for AI Coding Agent

## Hướng dẫn sử dụng

Mỗi prompt bên dưới là **self-contained** — chứa đủ context để AI thực hiện task mà không cần đọc toàn bộ tài liệu.

**Quy trình dùng:**
1. Copy toàn bộ nội dung trong khung `---PROMPT START---` / `---PROMPT END---`
2. Paste vào chat với AI
3. Sau khi AI trả lời xong, copy code vào đúng file theo đường dẫn đã chỉ định
4. Đánh dấu `[x]` trong `04_FEATURE_TASKS.md`

**Lưu ý quan trọng:**
- Chạy task theo đúng thứ tự (TASK-01 → 02 → 03...) vì có dependency
- Nếu AI hỏi thêm, chỉ cần reply thêm context từ file docs tương ứng
- Package name: `com.example.todolist`

---

---

## TASK-01 · Dependencies & Hilt Foundation

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Jetpack Compose, Clean Architecture.

**Nhiệm vụ:** Cấu hình toàn bộ dependencies và Hilt DI foundation cho dự án Android To-Do List.

**Package:** `com.example.todolist`
**Min SDK:** 26 | **Target SDK:** 35

**Files cần tạo/sửa:**
1. `app/build.gradle.kts` — toàn bộ dependencies
2. `build.gradle.kts` (root) — Hilt + KSP plugin classpath
3. `app/src/main/java/com/example/todolist/ToDoApplication.kt`
4. `app/src/main/java/com/example/todolist/di/DatabaseModule.kt`
5. `app/src/main/java/com/example/todolist/di/RepositoryModule.kt`
6. `app/src/main/java/com/example/todolist/di/NotificationModule.kt`
7. `app/src/main/java/com/example/todolist/core/common/Constants.kt`

**Dependencies cần thêm (dùng version catalog hoặc trực tiếp):**
- Room 2.6+ với KSP processor
- Hilt 2.51+ với hilt-navigation-compose
- Compose BOM 2024.x (Material3, Navigation Compose, Activity Compose)
- DataStore Preferences 1.1.x
- WorkManager 2.9.x với hilt-work
- Kotlin Coroutines + Flow 1.8.x
- Vico Charts (com.patrykandpatrick.vico:compose:latest)
- Firebase BoM với firebase-auth và firebase-firestore
- Google Credential Manager (androidx.credentials)
- Coil Compose (cho ảnh)

**Constants cần định nghĩa:**
```kotlin
object Constants {
    const val DB_NAME = "todolist.db"
    const val NOTIFICATION_CHANNEL_ID = "todolist_reminders"
    const val NOTIFICATION_CHANNEL_NAME = "Task Reminders"
    const val HABIT_BASE_SCORE = 15
    const val DAILY_BASE_SCORE = 10
    const val ONE_TIME_BASE_SCORE = 20
    const val ROLLOVER_WORK_NAME = "daily_rollover_worker"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_HABIT_NOTIF = "habit_notifications_enabled"
    const val PREF_EVENING_REMINDER = "evening_reminder_enabled"
    const val PREF_EVENING_TIME = "evening_reminder_time"
    const val PREF_SORT_ORDER = "task_sort_order"
}
```

**DatabaseModule cần provide:**
- `AppDatabase` (Singleton) — Room.databaseBuilder
- `TaskDao`, `CategoryDao`, `CompletionLogDao`, `ScoreDao` — từ AppDatabase

**RepositoryModule:** dùng @Binds để bind interface → impl (sẽ implement sau, tạm thời để placeholder comment)

**Yêu cầu output:** Viết code hoàn chỉnh cho tất cả 7 file. Đảm bảo build được, không có lỗi import.
---PROMPT END---

---

## TASK-02 · Core Utilities & Extensions

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Jetpack Compose, Clean Architecture.

**Nhiệm vụ:** Viết các utility class và extension functions dùng chung cho toàn app.

**Package:** `com.example.todolist`

**Context — Score System:**
- Habit Task hoàn thành = 15 điểm base
- Daily Task hoàn thành = 10 điểm base
- One-time Task hoàn thành = 20 điểm base
- Streak multiplier (chỉ Habit): 0-2 ngày=x1.0, 3-6=x1.5, 7-13=x2.0, 14-29=x2.5, 30+=x3.0

**Enums đã tồn tại (tham khảo):**
```kotlin
enum class TaskType { HABIT, DAILY, ONE_TIME }
enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class SortOrder { PRIORITY, DEADLINE, CATEGORY, MANUAL }
```

**Files cần tạo:**

**1. `core/common/Resource.kt`**
```kotlin
// Wrapper cho kết quả async operation
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val cause: Throwable? = null) : Resource<T>()
    class Loading<T> : Resource<T>()
}
// Thêm các extension: isSuccess, isError, isLoading, getOrNull(), getErrorOrNull()
```

**2. `core/utils/ScoreCalculator.kt`**
- `fun calculateScore(taskType: TaskType, currentStreak: Int): Int`
- `fun getStreakMultiplier(streak: Int): Float`
- Theo đúng bảng multiplier ở trên

**3. `core/utils/DateUtils.kt`**
- `fun getWeekRange(date: LocalDate): Pair<LocalDate, LocalDate>` — Thứ Hai đến Chủ Nhật
- `fun getMonthRange(date: LocalDate): Pair<LocalDate, LocalDate>` — ngày 1 đến cuối tháng
- `fun formatDateForDb(date: LocalDate): String` — "yyyy-MM-dd"
- `fun parseDateFromDb(str: String): LocalDate`
- `fun formatTimeForDb(time: LocalTime): String` — "HH:mm"
- `fun parseTimeFromDb(str: String): LocalTime`
- `fun localDateTimeToEpoch(dt: LocalDateTime): Long`
- `fun epochToLocalDateTime(epoch: Long): LocalDateTime`

**4. `core/extensions/DateExtensions.kt`**
- `fun LocalDate.isToday(): Boolean`
- `fun LocalDate.isTomorrow(): Boolean`
- `fun LocalDate.isYesterday(): Boolean`
- `fun LocalDate.toDisplayString(): String` — trả về "Hôm nay", "Ngày mai", "Hôm qua", hoặc format "dd/MM/yyyy"
- `fun LocalDateTime.toDisplayString(): String` — tương tự nhưng có giờ
- `fun LocalDate.daysBetween(other: LocalDate): Long`

**Yêu cầu output:** Viết code hoàn chỉnh cho tất cả 4 file, có KDoc comment.
---PROMPT END---

---

## TASK-03 · Repository Interfaces + Implementations

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Clean Architecture, Room Database.

**Nhiệm vụ:** Tạo Repository interfaces (Domain layer) và implementations (Data layer).

**Package:** `com.example.todolist`

**Domain Models đã tồn tại** (tham khảo khi viết interface):
```kotlin
// Task là sealed class
sealed class Task {
    abstract val id: String; abstract val title: String
    abstract val note: String?; abstract val categoryId: String?
    abstract val priority: Priority; abstract val isCompleted: Boolean
    abstract val createdAt: LocalDateTime

    data class Habit(..., val currentStreak: Int, val longestStreak: Int,
                    val lastCompletedDate: LocalDate?, val recurrence: RecurrenceConfig,
                    val reminderTime: LocalTime?, val durationGoalMinutes: Int?) : Task()
    data class Daily(..., val scheduledDate: LocalDate, val deadlineTime: LocalTime?,
                    val rollOverCount: Int, val originalDate: LocalDate) : Task()
    data class OneTime(..., val deadline: LocalDateTime,
                      val reminderMinutesBefore: Int?, val completedAt: LocalDateTime?) : Task()
}
data class Category(val id: String, val name: String, val colorHex: String, val createdAt: LocalDateTime)
data class ScoreRecord(val id: String, val date: LocalDate, val pointsEarned: Int,
                       val tasksCompleted: Int, val tasksTotal: Int)
data class CompletionLog(val id: String, val taskId: String, val taskTitle: String,
                         val taskType: TaskType, val completedAt: LocalDateTime,
                         val pointsGained: Int, val streakAtCompletion: Int = 0)
```

**Mappers đã tồn tại:**
- `TaskEntity.toDomain(): Task`
- `Task.toEntity(): TaskEntity`
- `CategoryEntity.toDomain(): Category`, `Category.toEntity(): CategoryEntity`
- `CompletionLogEntity.toDomain(): CompletionLog`, `CompletionLog.toEntity(): CompletionLogEntity`
- `ScoreRecordEntity.toDomain(): ScoreRecord`, `ScoreRecord.toEntity(): ScoreRecordEntity`

**Files cần tạo:**

**1. `domain/repository/TaskRepository.kt`** (interface)
```kotlin
interface TaskRepository {
    fun observeHabitTasksForToday(dayOfWeek: Int): Flow<List<Task>>
    fun observeDailyTasksForDate(date: LocalDate): Flow<List<Task>>
    fun observeActiveOneTimeTasks(): Flow<List<Task>>
    fun observeTasksByCategory(categoryId: String): Flow<List<Task>>
    fun observeTaskById(taskId: String): Flow<Task?>
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun getTaskById(taskId: String): Task?
    suspend fun getUncompletedDailyBefore(date: LocalDate): List<Task>
    suspend fun resetHabitTasksCompletion()
    suspend fun getAllTasks(): List<Task>
}
```

**2. `domain/repository/CategoryRepository.kt`** (interface)
- CRUD: createCategory, updateCategory, deleteCategory
- observe: observeAllCategories(): Flow, getCategoryById(), isNameExists(name): Boolean

**3. `domain/repository/ScoreRepository.kt`** (interface)
- observeRecordForDate(date): Flow<ScoreRecord?>
- observeRecordsBetweenDates(from, to): Flow<List<ScoreRecord>>
- observeTotalPointsBetweenDates(from, to): Flow<Int>
- observeAllTimePoints(): Flow<Int>
- suspend addPointsForToday(points: Int) — upsert ScoreRecord
- suspend getOrCreateTodayRecord(): ScoreRecord

**4. `domain/repository/CompletionLogRepository.kt`** (interface)
- suspend insertLog(log: CompletionLog)
- observeLogsForDate(date): Flow<List<CompletionLog>>
- observeLogsBetweenDates(from, to): Flow<List<CompletionLog>>
- suspend getActivityCountByDate(from, to): List<DateActivityCount> — dùng cho heatmap
- suspend getHabitTaskLogs(taskId): List<CompletionLog>
- observeTotalPointsBetweenDates(from, to): Flow<Int>

**5-8. Implementations** trong `data/repository/`:
- `TaskRepositoryImpl.kt` — inject TaskDao, dùng TaskMapper
- `CategoryRepositoryImpl.kt` — inject CategoryDao
- `ScoreRepositoryImpl.kt` — inject ScoreDao, xử lý upsert logic trong addPointsForToday
- `CompletionLogRepositoryImpl.kt` — inject CompletionLogDao

**`ScoreRepositoryImpl.addPointsForToday` logic:**
```kotlin
// Lấy record hôm nay (hoặc tạo mới nếu chưa có)
// Cộng points vào pointsEarned, tăng tasksCompleted
// Upsert lại vào DB
```

**Yêu cầu output:** Viết code hoàn chỉnh, @HiltViewModel annotations đúng chỗ, xử lý coroutines đúng (suspend / Flow).
---PROMPT END---

---

## TASK-04 · UserPreferences DataStore

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Jetpack Compose, DataStore.

**Nhiệm vụ:** Implement DataStore Preferences để lưu cài đặt người dùng.

**Package:** `com.example.todolist`

**Enums cần định nghĩa (nếu chưa có):**
```kotlin
enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class SortOrder { PRIORITY, DEADLINE, CATEGORY, MANUAL }
```

**DataStore Keys (từ Constants):**
```kotlin
const val PREF_THEME_MODE = "theme_mode"
const val PREF_HABIT_NOTIF = "habit_notifications_enabled"
const val PREF_EVENING_REMINDER = "evening_reminder_enabled"
const val PREF_EVENING_TIME = "evening_reminder_time"  // "HH:mm"
const val PREF_SORT_ORDER = "task_sort_order"
```

**Files cần tạo:**

**1. `data/preferences/UserPreferences.kt`**
```kotlin
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val habitNotificationsEnabled: Boolean = true,
    val dailyEveningReminderEnabled: Boolean = true,
    val eveningReminderTime: String = "21:00",
    val taskSortOrder: SortOrder = SortOrder.PRIORITY
)
```

**2. `data/preferences/UserPreferencesDataStore.kt`**
```kotlin
@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.createDataStore(name = "user_prefs")

    val userPreferences: Flow<UserPreferences>  // map từ Preferences → UserPreferences

    suspend fun updateTheme(mode: ThemeMode)
    suspend fun updateHabitNotification(enabled: Boolean)
    suspend fun updateEveningReminder(enabled: Boolean, time: String)
    suspend fun updateSortOrder(order: SortOrder)
}
```

**3. Update `di/RepositoryModule.kt`:**
- Thêm `@Provides @Singleton` cho `DataStore<Preferences>`
- Provide `UserPreferencesDataStore`

**Yêu cầu output:** Code hoàn chỉnh 3 file, dùng `androidx.datastore.preferences.core.*`, handle IOException khi đọc preferences.
---PROMPT END---

---

## TASK-05 · Task CRUD Use Cases

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Clean Architecture, Coroutines.

**Nhiệm vụ:** Viết các Use Cases xử lý CRUD cho Task (Domain layer, thuần Kotlin).

**Package:** `com.example.todolist`

**Interfaces đã có:**
```kotlin
interface TaskRepository {
    fun observeHabitTasksForToday(dayOfWeek: Int): Flow<List<Task>>
    fun observeDailyTasksForDate(date: LocalDate): Flow<List<Task>>
    fun observeActiveOneTimeTasks(): Flow<List<Task>>
    suspend fun createTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(taskId: String)
    suspend fun getTaskById(taskId: String): Task?
    suspend fun getAllTasks(): List<Task>
}
```

**Sealed class Task:** Habit | Daily | OneTime (xem TASK-03 prompt để biết fields)

**Result class:**
```kotlin
data class TodayTasksResult(
    val habitTasks: List<Task.Habit>,
    val dailyTasks: List<Task.Daily>,
    val oneTimeTasks: List<Task.OneTime>
)
```

**Files cần tạo:**

**1. `domain/usecase/task/GetTodayTasksUseCase.kt`**
- combine 3 Flow từ repository thành 1 Flow<TodayTasksResult>
- dayOfWeek lấy từ LocalDate.now().dayOfWeek.value (1=T2, 7=CN)

**2. `domain/usecase/task/CreateTaskUseCase.kt`**
- Validate: title không được rỗng → throw IllegalArgumentException
- Nếu task.id rỗng → gán UUID.randomUUID().toString()
- Với Daily Task: nếu scheduledDate == null → set = LocalDate.now()
- Gọi taskRepository.createTask(task)
- Trả về Task đã được tạo (với ID mới)

**3. `domain/usecase/task/UpdateTaskUseCase.kt`**
- Validate title không rỗng
- Gọi taskRepository.updateTask(task)

**4. `domain/usecase/task/DeleteTaskUseCase.kt`**
- Gọi taskRepository.deleteTask(taskId)
- Trả về Task đã xóa (lấy trước khi xóa để support undo)

**5. `domain/usecase/task/GetTasksByDateRangeUseCase.kt`**
- Param: fromDate: LocalDate, toDate: LocalDate
- Combine logs + daily tasks trong khoảng ngày đó
- Dùng cho màn hình lịch sử

**Quy tắc Use Case:**
- Mỗi class có `operator fun invoke(...)` duy nhất
- Không có Android SDK import
- Inject repository qua constructor (Hilt)
- Trả về Flow hoặc suspend fun

**Yêu cầu output:** Code hoàn chỉnh 5 file, có KDoc, xử lý edge cases.
---PROMPT END---

---

## TASK-06 · Complete Task + Score Use Cases

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Clean Architecture, Coroutines.

**Nhiệm vụ:** Viết Use Cases xử lý tick hoàn thành task — đây là flow nghiệp vụ phức tạp nhất.

**Package:** `com.example.todolist`

**Score Logic:**
```kotlin
// Base points
HABIT = 15, DAILY = 10, ONE_TIME = 20
// Streak multiplier (chỉ áp cho HABIT)
0-2 ngày: x1.0 | 3-6: x1.5 | 7-13: x2.0 | 14-29: x2.5 | 30+: x3.0
```

**Interfaces đã có:**
```kotlin
interface TaskRepository {
    suspend fun updateTask(task: Task)
}
interface ScoreRepository {
    suspend fun addPointsForToday(points: Int)
}
interface CompletionLogRepository {
    suspend fun insertLog(log: CompletionLog)
}
```

**Models:**
```kotlin
data class CompletionLog(val id: String, val taskId: String, val taskTitle: String,
    val taskType: TaskType, val completedAt: LocalDateTime,
    val pointsGained: Int, val streakAtCompletion: Int = 0)

data class CompleteTaskResult(val pointsGained: Int, val newStreak: Int)
```

**Files cần tạo:**

**1. `domain/usecase/habit/UpdateStreakUseCase.kt`**
```kotlin
// Tính streak mới của Habit Task
// Nếu lastCompletedDate == hôm qua → streak + 1
// Nếu lastCompletedDate == hôm nay → không thay đổi (đã tick rồi)
// Còn lại (bỏ qua ≥1 ngày, hoặc chưa từng làm) → streak = 1
suspend operator fun invoke(habit: Task.Habit): Int
```

**2. `domain/usecase/score/CalculateScoreUseCase.kt`**
```kotlin
// Tính điểm = base * multiplier
// Với DAILY và ONE_TIME: multiplier luôn = 1.0
operator fun invoke(taskType: TaskType, currentStreak: Int): Int
```

**3. `domain/usecase/task/CompleteTaskUseCase.kt`** — **Orchestration chính**
```kotlin
// 5 bước theo thứ tự:
// Step 1: Tính streak mới (chỉ Habit, dùng UpdateStreakUseCase)
// Step 2: Tính điểm (dùng CalculateScoreUseCase)
// Step 3: Build updatedTask:
//   - Habit: isCompleted=true, currentStreak=newStreak,
//            longestStreak=max(old,new), lastCompletedDate=today
//   - Daily: isCompleted=true
//   - OneTime: isCompleted=true, completedAt=now
// Step 4: taskRepository.updateTask(updatedTask)
// Step 5: completionLogRepository.insertLog(...)
// Step 6: scoreRepository.addPointsForToday(points)
// Return: CompleteTaskResult(pointsGained, newStreak)
suspend operator fun invoke(task: Task): CompleteTaskResult
```

**4. `domain/usecase/score/GetTotalScoreUseCase.kt`**
```kotlin
// Lấy tổng điểm theo khoảng thời gian
// Param: DateRange enum (DAY, WEEK, MONTH, ALL_TIME)
operator fun invoke(range: DateRange): Flow<Int>
// Tính from/to date tương ứng rồi gọi scoreRepository.observeTotalPointsBetweenDates
```

**Yêu cầu output:** Code hoàn chỉnh 4 file. Đặc biệt chú ý bước 3 của CompleteTaskUseCase phải handle đúng từng type của sealed class Task.
---PROMPT END---

---

## TASK-07 · Roll-over Use Case + WorkManager

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, WorkManager, Coroutines.

**Nhiệm vụ:** Implement cơ chế tự động đẩy Daily Task chưa hoàn thành sang ngày hôm sau.

**Package:** `com.example.todolist`

**Interfaces đã có:**
```kotlin
interface TaskRepository {
    suspend fun getUncompletedDailyBefore(date: LocalDate): List<Task>
    suspend fun updateTask(task: Task)
    suspend fun resetHabitTasksCompletion()  // set is_completed = false cho tất cả Habit
}
```

**Task.Daily:**
```kotlin
data class Daily(
    ...,
    val scheduledDate: LocalDate,
    val rollOverCount: Int,
    val originalDate: LocalDate
) : Task()
```

**Files cần tạo:**

**1. `domain/usecase/task/RollOverTasksUseCase.kt`**
```kotlin
// Logic:
// 1. Lấy tất cả Daily Task chưa hoàn thành (is_completed=false) có scheduled_date < hôm nay
// 2. Với mỗi task: copy với scheduledDate = today, rollOverCount++
// 3. taskRepository.updateTask() cho từng task đã cập nhật
// 4. taskRepository.resetHabitTasksCompletion() — reset Habit Task cho ngày mới
suspend operator fun invoke()
```

**2. `notification/DailyRollOverWorker.kt`**
```kotlin
// CoroutineWorker với Hilt injection
@HiltWorker
class DailyRollOverWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val rollOverTasksUseCase: RollOverTasksUseCase
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result
    companion object {
        const val WORK_NAME = "daily_rollover_worker"
        // Hàm schedule(): PeriodicWorkRequest mỗi 24h
        // Initial delay: tính số ms đến 00:01 sáng hôm sau
        fun schedule(workManager: WorkManager)
        fun cancel(workManager: WorkManager)
    }
}
```

**3. Update `ToDoApplication.kt`:**
- Sau khi app khởi động: gọi `DailyRollOverWorker.schedule(WorkManager.getInstance(this))`
- Dùng `ExistingPeriodicWorkPolicy.KEEP` để không tạo duplicate

**Tính initial delay:**
```kotlin
// Cách tính delay đến 00:01 sáng hôm sau
val now = LocalDateTime.now()
val nextMidnight = now.toLocalDate().plusDays(1).atTime(0, 1)
val delayMs = ChronoUnit.MILLIS.between(now, nextMidnight)
```

**Yêu cầu output:** Code hoàn chỉnh 3 file. Đảm bảo WorkManager được configure đúng với Hilt (HiltWorkerFactory).
---PROMPT END---

---

## TASK-08 · Statistics Use Cases

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Clean Architecture, Coroutines, Flow.

**Nhiệm vụ:** Viết Use Cases tổng hợp dữ liệu thống kê cho Dashboard.

**Package:** `com.example.todolist`

**Models đã có:**
```kotlin
data class DailyStats(val date: LocalDate, val totalTasks: Int, val completedTasks: Int,
    val totalPoints: Int, val habitCompletions: Int, val dailyCompletions: Int, val oneTimeCompletions: Int)
data class HeatmapEntry(val date: LocalDate, val activityCount: Int)
data class HabitStreakStat(val taskId: String, val taskTitle: String, val currentStreak: Int, val longestStreak: Int)
data class SkippedTaskStat(val taskId: String, val taskTitle: String, val rollOverCount: Int, val taskType: TaskType)
enum class DateRange { DAY, WEEK, MONTH, ALL_TIME }
data class DateActivityCount(val completed_date: String, val count: Int)
```

**Interfaces đã có:**
```kotlin
interface CompletionLogRepository {
    fun observeLogsBetweenDates(from: LocalDate, to: LocalDate): Flow<List<CompletionLog>>
    suspend fun getActivityCountByDate(from: LocalDate, to: LocalDate): List<DateActivityCount>
    suspend fun getHabitTaskLogs(taskId: String): List<CompletionLog>
}
interface TaskRepository {
    fun observeHabitTasksForToday(dayOfWeek: Int): Flow<List<Task>>
}
interface ScoreRepository {
    fun observeRecordsBetweenDates(from: LocalDate, to: LocalDate): Flow<List<ScoreRecord>>
    fun observeTotalPointsBetweenDates(from: LocalDate, to: LocalDate): Flow<Int>
    fun observeAllTimePoints(): Flow<Int>
}
```

**Files cần tạo:**

**1. `domain/usecase/stats/GetDailyStatsUseCase.kt`**
- Param: DateRange
- Tính from/to date theo range
- Combine ScoreRecord + CompletionLog → List<DailyStats>
- Return: Flow<List<DailyStats>>

**2. `domain/usecase/stats/GetHeatmapDataUseCase.kt`**
- Param: monthsBack: Int = 3 (3 tháng gần nhất)
- Gọi completionLogRepository.getActivityCountByDate
- Map sang List<HeatmapEntry>
- Return: Flow<List<HeatmapEntry>>

**3. `domain/usecase/stats/GetHabitStreakStatsUseCase.kt`**
- Observe tất cả Habit Task
- Map sang List<HabitStreakStat>
- Sort by currentStreak DESC
- Return: Flow<List<HabitStreakStat>>

**4. `domain/usecase/stats/GetMostSkippedTasksUseCase.kt`**
- Observe tất cả Daily Task (kể cả đã hoàn thành)
- Filter rollOverCount > 0
- Sort by rollOverCount DESC, take top 10
- Map sang List<SkippedTaskStat>
- Return: Flow<List<SkippedTaskStat>>

**5. `domain/usecase/stats/GetCompletionHistoryUseCase.kt`**
- Param: DateRange
- Gọi completionLogRepository.observeLogsBetweenDates
- Return: Flow<List<CompletionLog>> sorted by completedAt DESC

**Yêu cầu output:** Code hoàn chỉnh 5 file, không có Android SDK import, chỉ dùng Kotlin + Flow.
---PROMPT END---

---

## TASK-09 · Notification Infrastructure

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Android Notification, AlarmManager.

**Nhiệm vụ:** Xây dựng toàn bộ hệ thống thông báo đẩy.

**Package:** `com.example.todolist`

**Constants đã có:**
```kotlin
const val NOTIFICATION_CHANNEL_ID = "todolist_reminders"
const val NOTIFICATION_CHANNEL_NAME = "Task Reminders"
```

**Files cần tạo:**

**1. `notification/NotificationHelper.kt`**
```kotlin
object NotificationHelper {
    fun createNotificationChannel(context: Context)
    // Tạo channel với importance = IMPORTANCE_HIGH

    fun showHabitReminder(context: Context, taskId: String, taskTitle: String)
    // Notification với: title="⏰ Nhắc nhở thói quen", body=taskTitle
    // Action: "Hoàn thành" → PendingIntent → CompleteTaskReceiver
    // Tap notification → mở TaskDetail (deep link: todolist://task/{taskId})

    fun showDeadlineReminder(context: Context, taskId: String, taskTitle: String, minutesLeft: Int)
    // Notification với countdown: "còn {minutesLeft} phút!"

    fun showEveningReminder(context: Context, pendingTaskCount: Int)
    // Notification tổng kết buổi tối
}
```

**2. `notification/NotificationScheduler.kt`**
```kotlin
@Singleton
class NotificationScheduler @Inject constructor(@ApplicationContext private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun scheduleHabitReminder(taskId: String, taskTitle: String, time: LocalTime)
    // AlarmManager.setRepeating() với interval = AlarmManager.INTERVAL_DAY
    // Đặt lần đầu vào giờ [time] hôm nay (nếu đã qua thì hôm mai)

    fun scheduleOneTimeReminder(taskId: String, taskTitle: String,
                                 deadline: LocalDateTime, minutesBefore: Int)
    // AlarmManager.setExactAndAllowWhileIdle()
    // Thời điểm trigger = deadline - minutesBefore phút

    fun cancelReminder(taskId: String)
    // Hủy PendingIntent theo taskId

    private fun buildPendingIntent(taskId: String, taskTitle: String, type: String): PendingIntent
}
```

**3. `notification/TaskReminderReceiver.kt`**
```kotlin
class TaskReminderReceiver : BroadcastReceiver() {
    // Nhận Intent từ AlarmManager
    // Extra: "task_id", "task_title", "reminder_type" ("habit"/"deadline"/"evening")
    // Gọi NotificationHelper tương ứng
    override fun onReceive(context: Context, intent: Intent)
}
```

**4. Cập nhật `AndroidManifest.xml`:**
```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
<uses-permission android:name="android.permission.USE_EXACT_ALARM"/>

<receiver android:name=".notification.TaskReminderReceiver"
    android:exported="false"/>
```

**Yêu cầu output:** Code hoàn chỉnh 3 file + đoạn XML manifest. Đảm bảo xử lý đúng Android 12+ exact alarm permission.
---PROMPT END---

---

## TASK-10 · App Theme & Design System

---PROMPT START---
Bạn là Android/UI Developer chuyên nghiệp, thành thạo Jetpack Compose, Material3.

**Nhiệm vụ:** Xây dựng design system hoàn chỉnh cho app To-Do List với phong cách tối giản, premium.

**Package:** `com.example.todolist.presentation.theme`

**Palette chính:**
- Primary: Indigo `#6366F1` (light) / `#818CF8` (dark)
- PrimaryContainer: `#4338CA` / `#3730A3`
- Secondary: Violet `#8B5CF6` / `#A78BFA`
- Background: `#FFFFFF` / `#0F0F14`
- Surface: `#F8F8FF` / `#1A1A24`
- SurfaceVariant: `#F1F0FF` / `#252535`
- Accent Streak/Score: Amber `#F59E0B`
- Success/Complete: Emerald `#10B981`
- Error/Overdue: Rose `#F43F5E`
- OnPrimary: `#FFFFFF` / `#1E1B4B`
- OnBackground: `#0F0E17` / `#E8E8F0`

**Files cần tạo:**

**1. `presentation/theme/Color.kt`**
- Định nghĩa tất cả màu ở trên dưới dạng `val ColorName = Color(0xFF...)`
- Thêm: `val StreakAmber`, `val SuccessGreen`, `val OverdueRed`, `val RollOverYellow`

**2. `presentation/theme/Typography.kt`**
- Font family: Inter (Google Fonts) — import qua `FontFamily`
- Các text styles: displayLarge, titleLarge, titleMedium, bodyLarge, bodyMedium, labelSmall
- Định nghĩa `val AppTypography = Typography(...)`

**3. `presentation/theme/Shape.kt`**
- Small: 8.dp (chips, badges)
- Medium: 16.dp (cards, bottom sheet)
- Large: 24.dp (dialogs, FAB)
- ExtraLarge: 32.dp (bottom navigation)
- Định nghĩa `val AppShapes = Shapes(...)`

**4. `presentation/theme/Theme.kt`**
```kotlin
@Composable
fun ToDoListTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    // Wrap content trong MaterialTheme với color scheme, typography, shapes
}
```

**Yêu cầu output:** Code hoàn chỉnh 4 file. Typography phải dùng Google Fonts Inter. Color scheme phải đủ cả light và dark.
---PROMPT END---

---

## TASK-11 · Navigation & Bottom Bar

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Jetpack Compose Navigation.

**Nhiệm vụ:** Setup hệ thống navigation với bottom bar 4 tabs và deep link từ notification.

**Package:** `com.example.todolist.presentation.navigation`

**Files cần tạo:**

**1. `presentation/navigation/Screen.kt`**
```kotlin
sealed class Screen(val route: String) {
    object Home       : Screen("home")
    object Dashboard  : Screen("dashboard")
    object Categories : Screen("categories")
    object Settings   : Screen("settings")
    object AddTask    : Screen("add_task")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
        const val ARG_TASK_ID = "taskId"
    }
}

// Bottom nav items
data class BottomNavItem(val screen: Screen, val label: String, val icon: ImageVector, val selectedIcon: ImageVector)
val bottomNavItems = listOf(Home, Dashboard, Categories, Settings) với icons tương ứng
```

**2. `presentation/navigation/BottomNavBar.kt`**
- Composable hiển thị 4 tabs
- Highlight tab đang active
- Animation khi chuyển tab (scale + fade)
- Icons: Home=House, Dashboard=BarChart, Categories=Label, Settings=Settings

**3. `presentation/navigation/AppNavHost.kt`**
```kotlin
@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }
        composable(Screen.Categories.route) { CategoryScreen(navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController) }
        composable(Screen.AddTask.route) { AddTaskScreen(navController) }
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument(Screen.TaskDetail.ARG_TASK_ID) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "todolist://task/{taskId}" })
        ) { backStackEntry ->
            TaskDetailScreen(taskId = backStackEntry.arguments?.getString("taskId")!!, navController)
        }
    }
}
```

**4. Update `MainActivity.kt`:**
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(...) {
        // Observe themeMode từ UserPreferencesDataStore
        // Wrap content trong ToDoListTheme(themeMode = ...)
        // Scaffold với BottomNavBar + AppNavHost
    }
}
```

**Yêu cầu output:** Code hoàn chỉnh 4 file. Bottom bar phải có smooth animation, không re-create screen khi switch tab (dùng `saveState/restoreState`).
---PROMPT END---

---

## TASK-12 · Shared UI Components

---PROMPT START---
Bạn là Android/UI Developer chuyên nghiệp, thành thạo Jetpack Compose, Material3, animation.

**Nhiệm vụ:** Xây dựng bộ Composable components tái sử dụng cho toàn app.

**Package:** `com.example.todolist.presentation.components`

**Theme colors đã có:** StreakAmber, SuccessGreen, OverdueRed, RollOverYellow, Primary, Surface...

**Files cần tạo:**

**1. `TaskCard.kt`**
```kotlin
@Composable
fun TaskCard(
    task: Task,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onClick: (Task) -> Unit,
    modifier: Modifier = Modifier
)
// Specs:
// - SwipeToDismiss: phải=xanh/check → onComplete, trái=đỏ/trash → onDelete
// - task.isCompleted: text có strikethrough + opacity = 0.4f
// - task is Task.Daily && rollOverCount > 0: hiện badge vàng "📅 +{count} ngày"
// - task is Task.OneTime && deadline đã qua: hiện "Quá hạn 🔴"
// - Hiện PriorityChip, CategoryTag nếu có
// - Smooth animation khi swipe
```

**2. `ProgressRing.kt`**
```kotlin
@Composable
fun ProgressRing(
    progress: Float,        // 0.0 → 1.0
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
)
// Canvas-based circular progress
// Màu: đỏ <0.5, vàng 0.5-0.79, xanh >=0.8
// Animate progress thay đổi với animateFloatAsState
// Hiện text "X/Y" ở giữa
```

**3. `StreakBadge.kt`**
```kotlin
@Composable
fun StreakBadge(streak: Int, modifier: Modifier = Modifier)
// Hiện "🔥 {streak}" với background gradient Amber
// Scale animation khi streak tăng (LaunchedEffect)
// Ẩn nếu streak == 0
```

**4. `PriorityChip.kt`**
```kotlin
@Composable
fun PriorityChip(priority: Priority, modifier: Modifier = Modifier)
// HIGH: đỏ | MEDIUM: vàng | LOW: xanh lá
// Dạng filled small chip với text
```

**5. `CategoryTag.kt`**
```kotlin
@Composable
fun CategoryTag(category: Category?, modifier: Modifier = Modifier)
// Hiện dot màu + tên category
// Null-safe: không hiện gì nếu category == null
```

**6. `EmptyState.kt`**
```kotlin
@Composable
fun EmptyState(title: String, subtitle: String, modifier: Modifier = Modifier)
// Illustration (dùng vector drawable hoặc emoji lớn)
// Center-aligned, subtle animation
```

**7. `ConfirmDialog.kt`**
```kotlin
@Composable
fun ConfirmDialog(title: String, message: String,
    confirmText: String = "Xóa", onConfirm: () -> Unit, onDismiss: () -> Unit)
```

**8. `QuickAddFab.kt`**
```kotlin
@Composable
fun QuickAddFab(onTypeSelected: (TaskType) -> Unit)
// Extended FAB với icon +
// Khi nhấn: mở ModalBottomSheet chọn loại:
//   🔁 Thói quen (Habit)
//   📅 Hôm nay (Daily)
//   🎯 Có deadline (One-time)
// BottomSheet có animation slide-up
```

**Yêu cầu output:** Code hoàn chỉnh 8 file. TaskCard phải dùng `SwipeToDismiss` của Compose. ProgressRing dùng Canvas API. Tất cả có `@Preview`.
---PROMPT END---

---

## TASK-13 · Home Screen

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Jetpack Compose, ViewModel, StateFlow.

**Nhiệm vụ:** Xây dựng màn hình Home — màn hình chính của app.

**Package:** `com.example.todolist.presentation.home`

**Use Cases đã có:**
```kotlin
class GetTodayTasksUseCase { operator fun invoke(date: LocalDate): Flow<TodayTasksResult> }
class CompleteTaskUseCase { suspend operator fun invoke(task: Task): CompleteTaskResult }
class DeleteTaskUseCase { suspend operator fun invoke(taskId: String): Task? }
data class TodayTasksResult(val habitTasks, val dailyTasks, val oneTimeTasks)
data class CompleteTaskResult(val pointsGained: Int, val newStreak: Int)
```

**UiState:**
```kotlin
data class HomeUiState(
    val isLoading: Boolean = true,
    val habitTasks: List<Task.Habit> = emptyList(),
    val dailyTasks: List<Task.Daily> = emptyList(),
    val oneTimeTasks: List<Task.OneTime> = emptyList(),
    val todayScore: ScoreRecord? = null,
    val selectedCategoryFilter: String? = null,
    val sortOrder: SortOrder = SortOrder.PRIORITY,
    val showPointsAnimation: Boolean = false,
    val animationPoints: Int = 0,
    val animationStreak: Int? = null,
    val deletedTask: Task? = null,
    val showUndoSnackbar: Boolean = false,
    val errorMessage: String? = null
)
```

**Files cần tạo:**

**1. `HomeViewModel.kt`** — @HiltViewModel
- `init { }` observe GetTodayTasksUseCase + ScoreRepository
- `fun completeTask(task: Task)` → launch coroutine → trigger animation
- `fun deleteTask(task: Task)` → xóa + hiện undo snackbar
- `fun undoDelete()` → tạo lại task đã xóa
- `fun dismissAnimation()` → reset showPointsAnimation
- `fun filterByCategory(categoryId: String?)`

**2. `HomeUiState.kt`** — data class như trên

**3. `HomeScreen.kt`**
```kotlin
@Composable
fun HomeScreen(navController: NavController) {
    // Collect uiState từ HomeViewModel
    // Scaffold với:
    //   topBar: tên ngày hôm nay + icon filter/sort
    //   floatingActionButton: QuickAddFab → navigate to AddTask với type param
    //   snackbarHost: undo delete
    // Body: LazyColumn với 3 section (Habit, Daily, OneTime)
    // Overlay: animation "+15 ⭐" khi hoàn thành task
}
```

**4. `components/TodaySummaryHeader.kt`**
```kotlin
@Composable
fun TodaySummaryHeader(score: ScoreRecord?, habitCount: Int, dailyCount: Int, oneTimeCount: Int)
// ProgressRing lớn + text "Hôm nay: X/Y (Z%)"
// Màu theo completion rate
// Hiện tổng điểm hôm nay
```

**5. `components/HabitSection.kt`** — Section header + danh sách TaskCard Habit
**6. `components/DailySection.kt`** — Section header + danh sách (roll-over tasks ưu tiên đầu)
**7. `components/OneTimeSection.kt`** — Section header + danh sách sorted by deadline

**Animation "+15 ⭐":**
```kotlin
// AnimatedVisibility với slide-up + fade-out sau 1.5 giây
// Hiện ở center top của màn hình
```

**Yêu cầu output:** Code hoàn chỉnh 7 file. HomeScreen phải scroll mượt (LazyColumn), handle empty state cho từng section, có @Preview.
---PROMPT END---

---

## TASK-14 · Add Task Screen

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Jetpack Compose, ViewModel, Form UI.

**Nhiệm vụ:** Xây dựng màn hình tạo task mới dưới dạng ModalBottomSheet với quick add và expanded mode.

**Package:** `com.example.todolist.presentation.task`

**Use Cases đã có:**
```kotlin
class CreateTaskUseCase { suspend operator fun invoke(task: Task): Task }
```

**UiState:**
```kotlin
data class AddTaskUiState(
    val taskType: TaskType = TaskType.DAILY,
    val title: String = "",
    val note: String = "",
    val selectedCategoryId: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val recurrenceType: RecurrenceType = RecurrenceType.DAILY,
    val selectedDays: Set<Int> = emptySet(),       // {1..7}
    val reminderTime: LocalTime? = null,
    val durationGoalMinutes: Int? = null,
    val scheduledDate: LocalDate = LocalDate.now(),
    val deadlineTime: LocalTime? = null,
    val deadline: LocalDateTime? = null,
    val reminderMinutesBefore: Int? = null,
    val isExpanded: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val titleError: String? = null,
    val error: String? = null
)
```

**Files cần tạo:**

**1. `add/AddTaskViewModel.kt`** — @HiltViewModel
- `fun onTitleChange(title: String)`
- `fun onTaskTypeChange(type: TaskType)` — reset fields không liên quan
- `fun onToggleExpanded()`
- `fun saveTask()` — validate + build Task object + call CreateTaskUseCase
- `fun buildTask(): Task` — switch on taskType, map state fields → sealed class

**2. `add/AddTaskUiState.kt`**

**3. `add/AddTaskScreen.kt`**
```kotlin
@Composable
fun AddTaskScreen(navController: NavController, initialTaskType: TaskType = TaskType.DAILY) {
    // ModalBottomSheet
    // Quick mode (isExpanded=false): chỉ TextField title + TaskTypeSelector + Save button
    // Expanded mode: toàn bộ form
    // "Thêm thông tin" text button → toggle expanded
    // LaunchedEffect(uiState.isSaved) → auto close khi saved
}
```

**4. `add/components/TaskTypeSelector.kt`**
```kotlin
// 3 tab/chip: 🔁 Thói quen | 📅 Hôm nay | 🎯 Deadline
// Selected tab có background màu primary
```

**5. `add/components/RecurrencePicker.kt`**
```kotlin
// Hiện khi taskType == HABIT
// Radio: Hàng ngày | Chọn ngày
// Nếu "Chọn ngày": hiện 7 chip ngày trong tuần (T2-CN), multi-select
```

**6. `add/components/DeadlinePicker.kt`**
```kotlin
// DatePickerDialog + TimePickerDialog
// Hiện preview "Deadline: 15/06/2026 17:00"
```

**7. `add/components/PrioritySelector.kt`**
```kotlin
// 3 SegmentedButton: Thấp | Trung | Cao
// Màu theo priority level
```

**8. `detail/TaskDetailScreen.kt` + `detail/TaskDetailViewModel.kt`**
- Hiển thị chi tiết 1 task theo taskId
- Cho phép edit inline (toggle edit mode)
- Button "Hoàn thành" / "Chưa hoàn thành"
- Button xóa với ConfirmDialog

**Yêu cầu output:** Code hoàn chỉnh 8 file. AddTaskScreen phải dùng ModalBottomSheet, auto-close khi save thành công.
---PROMPT END---

---

## TASK-15 · Dashboard Screen

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Jetpack Compose, Vico Charts, StateFlow.

**Nhiệm vụ:** Xây dựng màn hình Dashboard với 6 widget thống kê.

**Package:** `com.example.todolist.presentation.dashboard`

**Use Cases đã có:**
```kotlin
class GetDailyStatsUseCase { operator fun invoke(range: DateRange): Flow<List<DailyStats>> }
class GetHeatmapDataUseCase { operator fun invoke(monthsBack: Int = 3): Flow<List<HeatmapEntry>> }
class GetHabitStreakStatsUseCase { operator fun invoke(): Flow<List<HabitStreakStat>> }
class GetMostSkippedTasksUseCase { operator fun invoke(): Flow<List<SkippedTaskStat>> }
class GetCompletionHistoryUseCase { operator fun invoke(range: DateRange): Flow<List<CompletionLog>> }
class GetTotalScoreUseCase { operator fun invoke(range: DateRange): Flow<Int> }
```

**UiState:**
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

**Files cần tạo:**

**1. `DashboardViewModel.kt`** — observe tất cả use cases, update state khi range thay đổi
**2. `DashboardUiState.kt`**
**3. `DashboardScreen.kt`** — LazyColumn chứa tất cả widgets + TabRow filter (Ngày/Tuần/Tháng)

**4. `components/CompletionBarChart.kt`**
- Dùng Vico `ColumnChart`
- X-axis: ngày, Y-axis: % hoàn thành (0-100)
- Màu bar theo completion rate

**5. `components/ActivityHeatmap.kt`**
- Grid 7 cột (ngày trong tuần) × N hàng (tuần)
- Màu: trắng=0, xanh nhạt=1-2, xanh=3-5, xanh đậm=6+
- Kiểu GitHub contribution graph

**6. `components/StreakLeaderboard.kt`**
- LazyColumn danh sách HabitStreakStat
- Mỗi item: tên task + current streak (🔥) + longest streak (🏆)

**7. `components/ScoreTrendChart.kt`**
- Vico `LineChart` hoặc `ColumnChart`
- Điểm tích lũy theo ngày trong range

**8. `components/SkippedTasksList.kt`**
- Danh sách SkippedTaskStat
- Badge số lần bị dời "🔄 x{count}"

**9. `components/HistoryList.kt`**
- Danh sách CompletionLog nhóm theo ngày
- Filter: Hôm nay / Tuần này / Tháng này

**Yêu cầu output:** Code hoàn chỉnh 9 file. ActivityHeatmap dùng Canvas/LazyGrid. Charts dùng Vico library.
---PROMPT END---

---

## TASK-16 · Settings & Category Screens

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Jetpack Compose, ViewModel, DataStore.

**Nhiệm vụ:** Xây dựng màn hình Settings và quản lý Categories.

**Package:** `com.example.todolist.presentation`

**DataStore đã có:**
```kotlin
class UserPreferencesDataStore {
    val userPreferences: Flow<UserPreferences>
    suspend fun updateTheme(mode: ThemeMode)
    suspend fun updateHabitNotification(enabled: Boolean)
    suspend fun updateEveningReminder(enabled: Boolean, time: String)
    suspend fun updateSortOrder(order: SortOrder)
}
```

**Interfaces đã có:**
```kotlin
interface CategoryRepository {
    fun observeAllCategories(): Flow<List<Category>>
    suspend fun createCategory(category: Category)
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun isNameExists(name: String): Boolean
}
```

**UiStates:**
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

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isAddDialogOpen: Boolean = false,
    val editingCategory: Category? = null,
    val newCategoryName: String = "",
    val selectedColor: String = "#6366F1",
    val nameError: String? = null,
    val isDeleteDialogOpen: Boolean = false,
    val categoryToDelete: Category? = null
)
```

**Files cần tạo:**

**Settings:**
**1. `settings/SettingsViewModel.kt`** — observe preferences, expose functions update
**2. `settings/SettingsScreen.kt`** — LazyColumn với 3 sections
**3. `settings/components/ThemeSection.kt`** — 3 radio: Sáng / Tối / Theo hệ thống
**4. `settings/components/NotificationSection.kt`** — Switch cho từng loại notification + TimePickerDialog
**5. `settings/components/AccountSection.kt`** — Trạng thái đăng nhập + button Sync (placeholder cho TASK-17)

**Categories:**
**6. `category/CategoryViewModel.kt`** — CRUD operations, validate duplicate name
**7. `category/CategoryScreen.kt`** — LazyColumn + FAB thêm mới + edit/delete actions
**8. `category/components/CategoryItem.kt`** — Row: color dot + name + edit/delete icons
**9. `category/components/ColorPickerDialog.kt`** — Grid 12 màu preset để chọn

**ColorPickerDialog preset colors:**
```kotlin
val presetColors = listOf(
    "#6366F1", "#8B5CF6", "#EC4899", "#EF4444",
    "#F59E0B", "#10B981", "#06B6D4", "#3B82F6",
    "#84CC16", "#F97316", "#A855F7", "#14B8A6"
)
```

**Yêu cầu output:** Code hoàn chỉnh 9 file. SettingsScreen và CategoryScreen phải handle loading state. CategoryScreen phải có swipe-to-delete hoặc trailing delete button.
---PROMPT END---

---

## TASK-17 · Firebase Auth + Cloud Sync

---PROMPT START---
Bạn là Android Developer chuyên nghiệp, thành thạo Kotlin, Firebase Firestore, Firebase Auth, Google Sign-In.

**Nhiệm vụ:** Tích hợp Google Sign-In tùy chọn và đồng bộ dữ liệu lên Firebase Firestore.

**Package:** `com.example.todolist`

**Domain models đã có:** Task (sealed), Category, CompletionLog, ScoreRecord

**Firestore Structure:**
```
users/{userId}/tasks/{taskId}
users/{userId}/categories/{categoryId}
users/{userId}/completion_logs/{logId}
users/{userId}/score_records/{date}
```

**Files cần tạo:**

**1. `data/remote/dto/TaskDto.kt`**
- Map 1-1 với TaskEntity nhưng dùng cho Firestore document
- Tất cả fields dạng String/Int/Long/Boolean (Firestore-friendly)
- Thêm `fun toEntity(): TaskEntity` và `fun TaskEntity.toDto(): TaskDto`

**2. `data/remote/dto/CategoryDto.kt`** — tương tự

**3. `data/remote/firebase/FirebaseAuthSource.kt`**
```kotlin
@Singleton
class FirebaseAuthSource @Inject constructor() {
    val currentUserId: String?
    val isSignedIn: Boolean
    val currentUserEmail: String?
    suspend fun signInWithGoogle(context: Context): Result<String>  // trả về userId
    suspend fun signOut()
    fun observeAuthState(): Flow<Boolean>
}
```

**4. `data/remote/firebase/FirebaseTaskSource.kt`**
```kotlin
@Singleton
class FirebaseTaskSource @Inject constructor(private val firestore: FirebaseFirestore) {
    suspend fun batchWrite(userId: String, tasks: List<TaskDto>)
    suspend fun getAllTasks(userId: String): List<TaskDto>
    suspend fun deleteTask(userId: String, taskId: String)
}
```

**5. `data/remote/firebase/FirebaseCategorySource.kt`** — tương tự TaskSource

**6. `domain/repository/SyncRepository.kt`** (interface)
```kotlin
interface SyncRepository {
    suspend fun signInWithGoogle(context: Context): Result<Unit>
    suspend fun signOut()
    fun observeSignInState(): Flow<Boolean>
    val currentUserEmail: String?
    fun syncToFirebase(): Flow<SyncProgress>
}

sealed class SyncProgress {
    data class Uploading(val entity: String, val count: Int) : SyncProgress()
    object Done : SyncProgress()
    data class Error(val message: String) : SyncProgress()
}
```

**7. `data/repository/SyncRepositoryImpl.kt`**
- Implement SyncRepository
- `syncToFirebase()`: upload tasks → categories → completion_logs → score_records theo chunks 500

**8. `domain/usecase/sync/SignInWithGoogleUseCase.kt`**
**9. `domain/usecase/sync/SyncToFirebaseUseCase.kt`**

**10. `di/NetworkModule.kt`**
```kotlin
@Provides @Singleton fun provideFirebaseAuth(): FirebaseAuth
@Provides @Singleton fun provideFirestore(): FirebaseFirestore
```

**11. Update `settings/components/AccountSection.kt`:**
- Khi nhấn "Đăng nhập" → trigger SignInWithGoogleUseCase
- Hiện progress khi sync
- Hiện email + trạng thái sync khi đã đăng nhập

**Yêu cầu output:** Code hoàn chỉnh 11 file. Đặc biệt chú ý xử lý lỗi network gracefully. SyncToFirebaseUseCase phải emit SyncProgress để UI hiện progress bar.
---PROMPT END---

---

## Ghi chú cho AI được giao task

Khi nhận prompt, hãy:
1. **Đọc kỹ** phần "Files cần tạo" và output đúng số lượng file
2. **Không bỏ qua** phần `import` — đảm bảo đủ import cho mỗi file
3. **Đặt đúng package** theo đường dẫn file
4. **Hỏi lại** nếu có ambiguity thay vì đoán sai
5. **Không thêm dependency mới** ngoài những gì đã liệt kê trong TASK-01
