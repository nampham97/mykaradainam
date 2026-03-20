# MyKaraDainam

Ứng dụng quản lý quán karaoke nội bộ — quản lý 2 phòng, hóa đơn, báo cáo, và AI tư vấn.

## Tính năng

### Quản lý phòng
- 2 phòng karaoke với bộ đếm thời gian trực tiếp
- Bắt đầu / Kết thúc phiên cho mỗi phòng
- Trạng thái phòng: TRỐNG → ĐANG HÁT → ĐÃ LẬP HÓA ĐƠN → XONG

### Thu thập hóa đơn (AI)
- **Camera OCR**: Chụp ảnh hóa đơn → AI đọc và trích xuất dữ liệu
- **Nhập giọng nói**: Ghi âm → Whisper chuyển thành text → AI cấu trúc dữ liệu
- Màn hình xác nhận cho phép sửa trước khi lưu
- Nhập thủ công khi không có mạng

### Báo cáo
- Tổng doanh thu (hôm nay / tháng này)
- Biểu đồ donut: doanh thu theo phòng, mặt hàng bán chạy
- Thống kê phòng: số phiên, thời lượng trung bình

### AI Tư vấn
- **Dự đoán tiền điện**: Tính toán dựa trên giờ sử dụng + thiết bị + biểu giá QĐ 1279
- **Gợi ý nhập hàng**: AI phân tích xu hướng bán hàng 7 ngày
- **Tóm tắt cuối ngày**: Báo cáo tự nhiên bằng tiếng Việt

### Cài đặt
- Quản lý thiết bị mỗi phòng (tên + công suất kW)
- Biểu giá điện (sửa được khi nhà nước thay đổi)

## Tech Stack

| Thành phần | Công nghệ |
|-----------|-----------|
| Ngôn ngữ | Kotlin 2.3.10 |
| UI | Jetpack Compose + Material 3 |
| Theme | Catppuccin Dark (Mocha) |
| Database | Room (SQLite) |
| API | Retrofit + OkHttp → Groq API |
| DI | Hilt |
| Camera | CameraX |
| Navigation | Navigation Compose (type-safe) |
| Charts | Custom Compose Canvas |

### Groq AI Models

| Mục đích | Model |
|---------|-------|
| OCR hóa đơn | meta-llama/llama-4-scout-17b-16e-instruct |
| Chuyển giọng nói | whisper-large-v3 |
| Orchestrator / AI Advisor | openai/gpt-oss-120b |

## Yêu cầu

- Android 8.0+ (API 26)
- JDK 17
- Android SDK (compileSdk 35)
- Groq API Key

## Cách chạy

### Cách 1: Android Studio (Khuyến nghị)

1. Cài [Android Studio](https://developer.android.com/studio) (phiên bản mới nhất)
2. Clone repo:
   ```bash
   git clone https://github.com/nampham97/mykaradainam.git
   cd mykaradainam
   ```
3. Thêm Groq API key vào `local.properties`:
   ```properties
   GROQ_API_KEY=gsk_your_key_here
   ```
4. Mở project trong Android Studio → Sync Gradle → Run

### Cách 2: VS Code + Command Line

VS Code có thể dùng được nhưng **không có Compose Preview** (xem trước UI). Phù hợp để chỉnh sửa code, build, và deploy.

#### Cài đặt cần thiết

1. **JDK 17**: [Download](https://adoptium.net/)
2. **Android SDK**: Cài thông qua Android Studio hoặc [command line tools](https://developer.android.com/studio#command-line-tools-only)
3. **VS Code Extensions**:
   - [Kotlin](https://marketplace.visualstudio.com/items?itemName=fwcd.kotlin) — code completion, syntax
   - [Android Tools](https://marketplace.visualstudio.com/items?itemName=levkosyk.vscode-android-tools) — ADB, emulator, Logcat

#### Cấu hình

Đặt biến môi trường:
```bash
# Windows (PowerShell)
$env:ANDROID_HOME = "C:\Users\<username>\AppData\Local\Android\Sdk"

# hoặc thêm vào local.properties
# sdk.dir=C:\\Users\\<username>\\AppData\\Local\\Android\\Sdk
```

#### Build & Run

```bash
# Clone và cấu hình
git clone https://github.com/nampham97/mykaradainam.git
cd mykaradainam
echo "GROQ_API_KEY=gsk_your_key_here" >> local.properties

# Build debug APK
./gradlew assembleDebug

# Kết nối điện thoại qua USB (bật USB Debugging) hoặc chạy emulator
adb devices

# Cài APK lên thiết bị
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

#### Tạo emulator (nếu chưa có)

```bash
# Liệt kê system images có sẵn
sdkmanager --list | grep "system-images"

# Cài system image
sdkmanager "system-images;android-35;google_apis;x86_64"

# Tạo AVD
avdmanager create avd -n MyKaraTest -k "system-images;android-35;google_apis;x86_64"

# Chạy emulator
emulator -avd MyKaraTest
```

### Cách 3: Command Line thuần (không cần IDE)

```bash
# Build
./gradlew assembleDebug

# Chạy tests
./gradlew test

# Cài lên thiết bị đã kết nối
./gradlew installDebug
```

## Hạn chế khi dùng VS Code

| Tính năng | Android Studio | VS Code |
|----------|---------------|---------|
| Compose Preview | ✅ Real-time | ❌ Không có |
| Layout Editor | ✅ Visual | ❌ Không có |
| Emulator tích hợp | ✅ Built-in | ⚠️ Qua extension |
| Build & Deploy | ✅ 1-click | ⚠️ Terminal commands |
| Debugging | ✅ Full | ⚠️ Hạn chế |
| Profiling | ✅ Full | ❌ Không có |
| Code completion | ✅ Excellent | ⚠️ Cơ bản |

**Khuyến nghị**: Dùng Android Studio để phát triển chính. VS Code phù hợp để chỉnh sửa nhanh hoặc review code.

## Cấu trúc dự án

```
app/src/main/java/com/mykaradainam/
├── ui/
│   ├── theme/        # Catppuccin Dark colors, typography
│   ├── components/   # RoomCard, DonutChart, TimerDisplay, ...
│   ├── home/         # Trang chủ — 2 phòng + hành động nhanh
│   ├── invoice/      # Camera OCR, Voice, Xác nhận hóa đơn
│   ├── reports/      # Báo cáo + AI Tư vấn
│   └── settings/     # Cài đặt thiết bị + biểu giá điện
├── data/
│   ├── local/        # Room DB, DAOs, Entities
│   ├── remote/       # Groq API service
│   └── repository/   # Repositories
├── model/            # RoomStatus enum
├── navigation/       # Type-safe routes
├── util/             # Currency, time, electricity formatters
└── di/               # Hilt modules
```

## Giờ hoạt động & Biểu giá điện

Quán hoạt động: **12:00 - 22:00**

| Khung giờ | Giờ | Giá (đ/kWh) |
|-----------|-----|-------------|
| Bình thường | 04:00 - 17:00 | 3.152 |
| Cao điểm | 17:00 - 20:00 | 5.422 |
| Bình thường | 20:00 - 22:00 | 3.152 |
| Thấp điểm | 22:00 - 04:00 | 1.918 |

Nguồn: QĐ 1279/QĐ-BCT (10/05/2025), hộ kinh doanh, dưới 6kV.

## License

Internal use only.
