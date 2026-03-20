# MyKara

Ứng dụng Android quản lý quán karaoke nội bộ — 2 phòng, hóa đơn AI, báo cáo doanh thu, tư vấn thông minh.

## Tính năng

**Quản lý phòng** — 2 phòng với timer thời gian thực, trạng thái: Trống → Đang hát → Đã lập hóa đơn → Xong

**Thu thập hóa đơn (AI)**
- Camera OCR: chụp hóa đơn → AI đọc và trích xuất
- Nhập giọng nói: ghi âm → Whisper → AI cấu trúc dữ liệu
- Màn hình xác nhận trước khi lưu
- Nhập thủ công khi offline

**Báo cáo** — Doanh thu hôm nay / tháng, biểu đồ donut (theo phòng, mặt hàng), thống kê phiên

**AI Tư vấn**
- Dự đoán tiền điện (giờ sử dụng × thiết bị × biểu giá QĐ 1279)
- Gợi ý nhập hàng (phân tích xu hướng 7 ngày)
- Tóm tắt cuối ngày bằng tiếng Việt

**Cài đặt** — Quản lý thiết bị (tên + công suất kW), biểu giá điện tùy chỉnh

## Tech stack

| | |
|---|---|
| Kotlin 2.3.20 | Jetpack Compose + Material 3 |
| Catppuccin Mocha | Room (SQLite) |
| Retrofit + OkHttp → Groq API | Hilt DI |
| CameraX | Navigation Compose (type-safe) |

**Groq AI models**

| Model | Dùng cho |
|---|---|
| `meta-llama/llama-4-scout-17b-16e-instruct` | OCR hóa đơn |
| `whisper-large-v3` | Chuyển giọng nói → text |
| `openai/gpt-oss-120b` | Orchestrator, AI Advisor |

## Yêu cầu

- Android 8.0+ (API 26)
- JDK 17
- Groq API Key

## Bắt đầu

```bash
git clone https://github.com/nampham97/mykaradainam.git
cd mykaradainam
```

Thêm API key vào `local.properties`:

```properties
GROQ_API_KEY=gsk_your_key_here
```

**Android Studio** (khuyến nghị): Mở project → Sync Gradle → Run

**Command line**:

```bash
./gradlew assembleDebug
./gradlew installDebug    # cài lên thiết bị đã kết nối
```

## Cấu trúc

```
app/src/main/java/com/mykaradainam/
├── ui/
│   ├── theme/        # Catppuccin colors, typography, shapes
│   ├── components/   # RoomCard, DonutChart, TimerDisplay, ...
│   ├── home/         # Trang chủ — 2 phòng + hành động nhanh
│   ├── invoice/      # Camera OCR, Voice, Xác nhận hóa đơn
│   ├── reports/      # Báo cáo + AI Tư vấn
│   └── settings/     # Thiết bị + biểu giá điện
├── data/
│   ├── local/        # Room DB, DAOs, Entities
│   ├── remote/       # Groq API service
│   └── repository/   # Repositories
├── model/            # RoomStatus enum
├── navigation/       # Type-safe routes
├── util/             # Currency, time, electricity formatters
└── di/               # Hilt modules
```

## Biểu giá điện

Giờ hoạt động: **12:00 – 22:00**

| Khung giờ | Giá (đ/kWh) |
|---|---|
| Bình thường 04:00–17:00, 20:00–22:00 | 3.152 |
| Cao điểm 17:00–20:00 | 5.422 |
| Thấp điểm 22:00–04:00 | 1.918 |

> QĐ 1279/QĐ-BCT (10/05/2025), hộ kinh doanh, dưới 6kV.

## License

Internal use only.
