# 01 — Product Requirements Document (PRD)
# Ứng dụng Android To-Do List Cá Nhân

**Phiên bản:** 1.0 MVP
**Ngày:** 01/06/2026
**Package:** `com.example.todolist`
**Target:** Google Play Store

---

## 1. Goal

Xây dựng ứng dụng Android To-Do List cá nhân giúp người dùng **tổ chức công việc hàng ngày có hệ thống**, phân biệt rõ 3 loại task khác nhau. Ứng dụng có cơ chế **tính điểm & streak** để tạo động lực, và **dashboard thống kê** để người dùng nhìn lại hiệu suất bản thân.

---

## 2. Target User

| Thuộc tính | Mô tả |
|---|---|
| **Đối tượng** | Cá nhân, dùng một mình trên một thiết bị Android |
| **Nhu cầu cốt lõi** | Kiểm soát công việc hàng ngày, xây dựng thói quen, không bỏ sót việc quan trọng |
| **Pain point** | App quá phức tạp/đơn giản; thiếu động lực; task bị quên do không nhắc nhở |
| **Kỳ vọng UX** | Mở app là thêm task ngay, giao diện sạch, dark/light mode, thống kê dễ đọc |

---

## 3. MVP Features

### 3.1 Ba loại Task

#### 🔁 Habit Task — Thói quen dài hạn
- Lặp lại: **hàng ngày** hoặc **hàng tuần** (chọn ngày cụ thể)
- Có thời gian nhắc nhở lặp lại (ví dụ: 08:00 mỗi ngày)
- Tick hoàn thành mỗi ngày → tích lũy **streak**
- Không hoàn thành trong ngày → **mất streak**, ngày sau xuất hiện lại bình thường
- Tùy chọn: đặt mục tiêu thời lượng (ví dụ: "2 tiếng/ngày")

#### 📅 Daily Task — Việc trong ngày
- Tạo nhanh cho hôm nay hoặc lên kế hoạch từ hôm trước
- Có thể gán: deadline giờ, ưu tiên (Cao/Trung/Thấp), danh mục, ghi chú
- **Roll-over:** Task chưa hoàn thành cuối ngày → tự đẩy sang ngày mai với label **"Từ hôm qua ⚠️"**
- Không giới hạn số lần roll-over

#### 🎯 One-time Task — Việc có deadline
- Deadline ngày giờ cụ thể
- Thông báo trước deadline (tùy chỉnh: 30 phút / 1 giờ / 1 ngày)
- Sau khi hoàn thành hoặc quá hạn → chuyển vào lịch sử

### 3.2 Quick Add
- FAB (+) → bottom sheet → chọn loại task
- Chỉ cần nhập **tiêu đề** là lưu được ngay
- Fields mở rộng: expandable, không bắt buộc

### 3.3 Hệ thống Điểm & Streak

#### Điểm cơ bản
| Loại task | Điểm |
|---|---|
| Habit Task | 15 điểm |
| Daily Task | 10 điểm |
| One-time Task | 20 điểm |

#### Streak Bonus (Habit Task)
| Streak | Hệ số |
|---|---|
| 0–2 ngày | x1.0 |
| 3–6 ngày | x1.5 |
| 7–13 ngày | x2.0 |
| 14–29 ngày | x2.5 |
| 30+ ngày | x3.0 |

#### Completion Rate
- Header: **"Hôm nay: X/Y task (Z%)"**
- Màu: 🔴 <50% / 🟡 50–79% / 🟢 ≥80%

### 3.4 Dashboard & Thống kê

| Widget | Mô tả |
|---|---|
| Completion Chart | Biểu đồ cột/vòng theo ngày/tuần/tháng |
| Streak Leaderboard | Streak dài nhất từng Habit Task |
| Score Trend | Điểm tích lũy theo tuần/tháng/all-time |
| Activity Heatmap | Kiểu GitHub — ngày làm nhiều task → màu đậm |
| Skipped Tasks | Task bị roll-over nhiều nhất |
| History | Danh sách đã hoàn thành — lọc ngày/tuần/tháng |

### 3.5 Notifications
- Habit Task: nhắc lặp lại theo giờ đã cài
- Daily Task: nhắc khi đến deadline giờ (nếu có)
- One-time Task: nhắc trước deadline (tùy chỉnh)
- Buổi tối (tùy chọn bật/tắt): **"Bạn còn X task chưa hoàn thành"**

### 3.6 Categories (Danh mục)
- Tự tạo danh mục + gán màu
- Lọc task theo danh mục ở Home

### 3.7 UI/UX
- Dark / Light mode (theo hệ thống hoặc thủ công)
- Sắp xếp: Ưu tiên / Deadline / Danh mục / Thủ công (kéo thả)
- Swipe phải → Hoàn thành | Swipe trái → Xóa

### 3.8 Storage & Sync
- **Default:** Offline, Room Database (không cần tài khoản)
- **Optional:** Google Sign-In → sync lên Firebase Firestore
- Merge local → cloud khi đăng nhập lần đầu

---

## 4. User Flows

### Flow 1 — Thêm Habit Task
```
Home → FAB (+) → Chọn "Habit"
  → Nhập tiêu đề (bắt buộc)
  → [Tùy chọn] Chu kỳ lặp / Giờ nhắc / Danh mục / Ghi chú
  → Lưu → Task xuất hiện + notification được lên lịch
```

### Flow 2 — Daily Task Roll-over
```
Home (ngày N)
  → Tick hoàn thành → +10 điểm
  → [23:59] Worker chạy → task chưa xong → clone sang ngày N+1
  → "Từ hôm qua ⚠️" badge hiển thị đầu danh sách ngày N+1
```

### Flow 3 — Xem Dashboard
```
Tab Thống kê
  → Completion ring hôm nay
  → Switch tab: Tuần / Tháng
  → Scroll: Heatmap → Streak board → Skipped tasks
  → "Lịch sử" → filter ngày/tuần/tháng
```

### Flow 4 — Bật Cloud Sync
```
Settings → "Tài khoản & Đồng bộ"
  → "Bật đồng bộ đám mây"
  → Google Sign-In dialog
  → Merge local → Firebase
  → "Đồng bộ thành công ✅"
```

### Flow 5 — One-time Task + Deadline Reminder
```
FAB (+) → "One-time" → Nhập title + deadline + nhắc trước
  → [Đến giờ nhắc] Push notification "⏰ còn 1 giờ!"
  → Tap notification → mở Task Detail
  → [Quá hạn chưa tick] → label "Quá hạn 🔴"
```

---

## 5. Out of Scope (v1)
- Cộng tác nhiều người
- Tệp đính kèm / hình ảnh
- Android Home Widget
- Xuất CSV / PDF
- AI gợi ý task

---

## 6. Acceptance Criteria

- [ ] Tạo được cả 3 loại task trong < 10 giây (chỉ nhập tiêu đề)
- [ ] Habit Task tự động nhắc đúng giờ đã cài
- [ ] Daily Task chưa hoàn thành tự roll-over với label rõ ràng
- [ ] Điểm và streak tính đúng sau khi tick
- [ ] Dashboard hiển thị đủ 6 loại widget thống kê
- [ ] App hoạt động hoàn toàn offline
- [ ] Google Sign-In + sync không mất dữ liệu local
- [ ] Dark/Light mode hoạt động đúng
