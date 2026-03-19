# MyKaraDainam — Design Specification

## Overview

MyKaraDainam is an internal Android app for managing a 2-room karaoke business. Employees use it to track room sessions (start/finish timers), capture invoices via camera OCR or voice input powered by Groq AI, and view business reports with AI-driven insights. No authentication required.

**Operating hours:** 12:00 - 22:00 (for electricity rate calculation only — app does not restrict usage outside these hours)

**Min SDK:** API 26 (Android 8.0)
**Target SDK:** API 35
**Timezone:** Asia/Ho_Chi_Minh (ICT, UTC+7). All epoch timestamps stored in UTC, displayed in ICT.
**"Today" boundary:** Calendar day in ICT (00:00 - 23:59 UTC+7).

## Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Kotlin | 2.3.10 |
| UI Framework | Jetpack Compose (Material 3) | BOM 2025.08.00 |
| Local Database | Room | Latest stable |
| Networking | Retrofit + OkHttp | Latest stable |
| Async | Kotlin Coroutines + Flow | Latest stable |
| Camera | CameraX | Latest stable |
| DI | Hilt | Latest stable |
| Charts | Compose Canvas (custom donut) | N/A |
| Navigation | Navigation Compose | Latest stable |
| AI API | Groq | REST API |

**Note on charts:** Donut charts will be drawn with Compose Canvas (custom composable) rather than a third-party library. This avoids dependency on libraries that may not support donut charts natively and keeps the app lightweight.

## Architecture

Single-module monolith with clean package separation.

```
com.mykaradainam/
├── ui/
│   ├── theme/           # Catppuccin Dark colors, typography, shapes
│   ├── home/            # Home dashboard (rooms + actions + stats)
│   ├── invoice/         # Invoice camera + voice input screens
│   ├── confirm/         # Confirm/edit AI-extracted data before saving
│   ├── reports/         # Dashboard with charts + AI advisor
│   ├── settings/        # Equipment wattage, electricity rates
│   └── components/      # Shared composables (cards, buttons, dialogs, donut chart)
├── data/
│   ├── local/
│   │   ├── db/          # Room DB, DAOs, entities
│   │   └── datastore/   # Preferences (simple key-value settings)
│   ├── remote/
│   │   └── groq/        # Groq API service (OCR, Whisper, orchestrator)
│   └── repository/      # Repositories bridging local + remote
├── model/               # Domain models (RoomSession, InvoiceItem, Equipment, ElectricityRate)
└── util/                # Extensions, formatters (currency ₫, time, electricity calc)
```

## Visual Design

**Theme:** Catppuccin Dark
- Background: `#1e1e2e`
- Surface: `#313244`
- Overlay: `#45475a`
- Text primary: `#cdd6f4`
- Text secondary: `#6c7086`
- Accent purple: `#cba6f7`
- Green (active/success): `#a6e3a1`
- Red (finish/alert): `#f38ba8`
- Blue (camera/room1): `#89b4fa`
- Yellow (reports/confirm): `#f9e2af`

**UI Language:** Vietnamese

## Data Model

### RoomSession

| Field | Type | Description |
|-------|------|-------------|
| id | Long (PK, auto) | Primary key |
| roomNumber | Int | 1 or 2 |
| startTime | Long | Epoch ms (UTC), when employee pressed Start |
| endTime | Long? | Epoch ms (UTC), null = active session |
| status | String | ACTIVE, INVOICED, or FINISHED |
| createdAt | Long | Epoch ms (UTC) |

**Note:** `totalAmount` is not stored on RoomSession. It is always computed by summing `InvoiceItem.subtotal` for the session. This avoids data inconsistency.

### InvoiceItem

| Field | Type | Description |
|-------|------|-------------|
| id | Long (PK, auto) | Primary key |
| sessionId | Long (FK → RoomSession) | Links to session |
| name | String | Item name (e.g. "Bia Tiger") |
| quantity | Int | Number of items |
| unitPrice | Long | Price per unit in VND |
| subtotal | Long | quantity × unitPrice in VND |

### Equipment

| Field | Type | Description |
|-------|------|-------------|
| id | Long (PK, auto) | Primary key |
| roomNumber | Int | 1 or 2 |
| name | String | Equipment name (e.g. "Điều hòa") |
| powerKw | Double | Power in kilowatts (e.g. 2.5) |

### ElectricityRate

| Field | Type | Description |
|-------|------|-------------|
| id | Long (PK, auto) | Primary key |
| tierName | String | "Cao điểm", "Bình thường", "Thấp điểm" |
| startHour | Int | Start hour (0-23) |
| endHour | Int | End hour (0-23) |
| ratePerKwh | Double | Price in VND per kWh |

**Pre-seeded rates (QĐ 1279/QĐ-BCT, hộ kinh doanh, dưới 6kV):**

| Tier | Hours | Rate (đ/kWh) |
|------|-------|-------------|
| Bình thường | 04:00 - 17:00 | 3.152 |
| Cao điểm | 17:00 - 20:00 | 5.422 |
| Bình thường | 20:00 - 22:00 | 3.152 |
| Thấp điểm | 22:00 - 04:00 | 1.918 |

**Note:** Full 24h rate coverage provided so electricity calculation works even if a session runs outside normal operating hours (12:00-22:00).

### Relationships

- `RoomSession` 1:N `InvoiceItem`
- All monetary values stored as `Long` in VND (no decimals needed)
- Exception: `ElectricityRate.ratePerKwh` and `Equipment.powerKw` use `Double` because they require decimal precision
- `Equipment` is per-room, managed in Settings
- `ElectricityRate` is global, pre-seeded, editable in Settings

## Session Lifecycle

```
FREE → [Employee presses Start] → ACTIVE (timer running)
                                      │
                    ┌─────────────────┼─────────────────┐
                    │                                     │
          [Press Invoice]                         [Press Finish]
          (camera or voice)                     (no invoice needed)
                    │                                     │
                    ▼                                     │
          Xác nhận screen                                 │
          (edit & save items)                             │
                    │                                     │
          [Save] → INVOICED                               │
                    │                                     │
          [Press Finish on Home]                          │
                    │                                     │
                    ▼                                     ▼
                FINISHED ←────────────────────────── FINISHED
                (endTime set, room becomes FREE)
```

**Key rules:**
- **Start** → creates `RoomSession` with `status=ACTIVE`, `startTime=now`
- **Invoice** → captures items, saves to DB, sets `status=INVOICED`. Room stays occupied (timer continues). Employee may add more invoices (additional items).
- **Finish** → sets `endTime=now`, `status=FINISHED`. Room becomes FREE on Home screen.
- Invoice and Finish are **independent actions**. An employee can finish without invoicing (e.g. free session), or invoice then finish later.
- If app is killed while a room is ACTIVE, on next launch the timer resumes based on `startTime` (it's just `now - startTime`).

## Screen Flow

```
Trang Chủ (Home)
├── Phòng card (tap room)
│   ├── Start → creates ACTIVE session (if FREE)
│   ├── Invoice → room selection is implicit (came from this room's card)
│   │   ├── 📷 Camera OCR → LLaMA Scout → Orchestrator → Xác nhận → Save
│   │   └── 🎙 Voice Input → Whisper → Orchestrator → Xác nhận → Save
│   └── Finish → sets endTime, status=FINISHED, room resets to FREE
├── Quick Actions (📷 / 🎙) → if only 1 room ACTIVE: auto-select it
│                             → if 2 rooms ACTIVE: show room picker dialog
│                             → if 0 rooms ACTIVE: show "Chưa có phòng hoạt động"
├── 📊 Báo cáo (Reports)
│   ├── Tab: Hôm nay (today)
│   ├── Tab: Tháng này (this month)
│   └── Tab: AI Tư vấn (AI advisor)
└── ⚙️ Cài đặt (Settings) — gear icon in top-right corner
    ├── Equipment wattage per room
    └── Electricity rate table
```

### Screen Details

#### 1. Trang Chủ (Home)
- Two room cards side-by-side showing status (ACTIVE with live timer / FREE with Start button)
- Active rooms show: timer counting up, Finish button, Invoice button
- Quick action grid: 📷 Scan Invoice, 🎙 Voice Input (with room context resolution — see flow above)
- Today's stats bar: total revenue (computed from InvoiceItems), session count, reports shortcut
- ⚙️ gear icon in top-right corner → Settings

#### 2. Camera OCR
- CameraX viewfinder to photograph invoice
- Capture → sends image as base64 to Groq LLaMA Scout (vision)
- Shows loading state while AI processes
- Result goes to Xác nhận screen
- Room context passed from previous screen (no room selection needed here)

#### 3. Voice Input
- Record button (hold or tap to start/stop)
- Audio recorded as m4a (confirmed compatible with Groq Whisper — supported formats: flac, mp3, mp4, mpeg, mpga, m4a, ogg, wav, webm)
- Transcribed Vietnamese text sent to Orchestrator (gpt-oss-120b) for structuring
- Result goes to Xác nhận screen

#### 4. Xác nhận (Confirm)
- Shared screen for both Camera and Voice paths
- Shows AI-extracted data: item list with quantities and unit prices, computed total
- Room number and session are pre-filled from navigation context (not editable — already determined)
- Employee can edit item names, quantities, unit prices, add/remove items
- If `confidence` is not "high" or `warnings` is non-empty → show yellow banner: "AI không chắc chắn, vui lòng kiểm tra kỹ" and highlight uncertain fields
- Save button → writes InvoiceItems to Room DB, sets session status=INVOICED → returns to Home
- **Manual entry fallback:** if AI fails, screen shows empty form where employee can manually type items

#### 5. Báo cáo (Reports)
- **Hôm nay / Tháng này tabs:**
  - Total revenue card with session count (computed via DAO aggregate queries)
  - Donut chart: revenue split by room (custom Compose Canvas)
  - Donut chart: top selling items with quantities (custom Compose Canvas)
  - Room usage stats: sessions per room, average duration, total hours
- **AI Tư vấn tab:** see AI Advisor section below

#### 6. Cài đặt (Settings)
- Equipment list per room (add/edit/delete items with name + wattage in kW)
- Electricity rate table (pre-filled, editable rows: tier name, hours, rate/kWh)
- Groq API key display (read-only, set at build time)
- Accessible from gear icon on Home screen

### Key DAO Queries (for Reports)

```
-- Total revenue today
SELECT SUM(i.subtotal) FROM InvoiceItem i
JOIN RoomSession s ON i.sessionId = s.id
WHERE s.createdAt >= :todayStartEpoch AND s.createdAt < :tomorrowStartEpoch

-- Revenue by room (today/month)
SELECT s.roomNumber, SUM(i.subtotal) FROM InvoiceItem i
JOIN RoomSession s ON i.sessionId = s.id
WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
GROUP BY s.roomNumber

-- Top selling items (today/month)
SELECT i.name, SUM(i.quantity) as totalQty, SUM(i.subtotal) as totalRevenue
FROM InvoiceItem i
JOIN RoomSession s ON i.sessionId = s.id
WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
GROUP BY i.name ORDER BY totalQty DESC LIMIT 10

-- Room usage stats
SELECT roomNumber, COUNT(*) as sessions,
       AVG(endTime - startTime) as avgDuration,
       SUM(endTime - startTime) as totalTime
FROM RoomSession
WHERE status = 'FINISHED' AND createdAt >= :startEpoch AND createdAt < :endEpoch
GROUP BY roomNumber

-- Sessions with hours breakdown (for electricity calc)
SELECT roomNumber, startTime, endTime FROM RoomSession
WHERE status = 'FINISHED' AND createdAt >= :startEpoch AND createdAt < :endEpoch
```

## Groq API Integration

### Models

| Purpose | Model | Endpoint |
|---------|-------|----------|
| OCR (invoice photos) | meta-llama/llama-4-scout-17b-16e-instruct | /chat/completions (vision) |
| Voice transcription | whisper-large-v3 | /audio/transcriptions |
| Orchestrator | openai/gpt-oss-120b | /chat/completions |

### API Configuration
- Base URL: `https://api.groq.com/openai/v1/`
- API key: stored in `local.properties` as `GROQ_API_KEY`, injected via `BuildConfig`
- Single Retrofit service: `GroqApiService`
- Headers: `Authorization: Bearer $GROQ_API_KEY`, `Content-Type: application/json` (or `multipart/form-data` for Whisper)

### Processing Flow

**Camera path:**
1. Capture photo via CameraX
2. Encode to base64
3. Send to LLaMA Scout (vision) with system prompt → structured JSON directly
4. If LLaMA Scout returns good structured data, skip orchestrator
5. If result needs cleanup → send to Orchestrator for re-structuring
6. Display in Xác nhận screen

**Voice path:**
1. Record audio (m4a format)
2. Send to Whisper as multipart/form-data (`file`, `model`, `language=vi`) → Vietnamese text
3. Send text to Orchestrator with system prompt → structured JSON
4. Display in Xác nhận screen

### API Request/Response Details

**LLaMA Scout (Vision) — Invoice OCR:**
```json
POST /chat/completions
{
  "model": "meta-llama/llama-4-scout-17b-16e-instruct",
  "messages": [{
    "role": "system",
    "content": "Bạn là trợ lý đọc hóa đơn karaoke. Trích xuất thông tin từ ảnh hóa đơn và trả về JSON với format: {\"items\": [{\"name\": string, \"quantity\": int, \"unitPrice\": int}], \"totalAmount\": int, \"confidence\": \"high\"|\"medium\"|\"low\", \"warnings\": [string]}. Chỉ trả về JSON, không giải thích."
  }, {
    "role": "user",
    "content": [
      {"type": "text", "text": "Đọc hóa đơn này:"},
      {"type": "image_url", "image_url": {"url": "data:image/jpeg;base64,{BASE64}"}}
    ]
  }],
  "temperature": 0.1
}
```

**Whisper — Voice Transcription:**
```
POST /audio/transcriptions
Content-Type: multipart/form-data
- file: (binary audio m4a)
- model: "whisper-large-v3"
- language: "vi"
```

**Orchestrator — Structure Voice Text:**
```json
POST /chat/completions
{
  "model": "openai/gpt-oss-120b",
  "messages": [{
    "role": "system",
    "content": "Bạn là trợ lý xử lý hóa đơn karaoke. Nhận mô tả bằng giọng nói (tiếng Việt) về hóa đơn và trả về JSON: {\"items\": [{\"name\": string, \"quantity\": int, \"unitPrice\": int}], \"totalAmount\": int, \"confidence\": \"high\"|\"medium\"|\"low\", \"warnings\": [string]}. Nếu không rõ giá, đặt unitPrice=0 và thêm warning. Chỉ trả về JSON."
  }, {
    "role": "user",
    "content": "{WHISPER_TRANSCRIPTION}"
  }],
  "temperature": 0.1
}
```

### Orchestrator Output Schema

```json
{
  "items": [
    { "name": "Bia Tiger", "quantity": 3, "unitPrice": 25000 },
    { "name": "Nước ngọt", "quantity": 2, "unitPrice": 15000 }
  ],
  "totalAmount": 105000,
  "confidence": "high",
  "warnings": []
}
```

**Note:** `roomNumber` and `startTime` are NOT in the AI output — they come from the session context (the room the employee navigated from). The AI only extracts item/pricing data from the invoice.

### Error Handling
- Network failure → "Không có kết nối" with retry button
- AI parsing failure → show raw text + empty manual form for employee to fill in
- Timeout → 30s default, retry once
- **Offline:** Sessions can be started/finished offline (no API needed). Invoice capture requires internet (Groq API). If offline, show manual entry form as fallback.
- **Image size:** Camera captures are resized to max 1280px on longest edge and compressed to JPEG quality 80 before base64 encoding (keeps payload under ~1MB)
- **Audio limit:** Voice recording capped at 120 seconds (keeps m4a file well under Groq's 25MB limit)
- **AI totalAmount validation:** The AI's `totalAmount` is displayed on Confirm screen but cross-checked against sum of `items[].quantity * items[].unitPrice`. If mismatch, show warning and use the computed sum.

## AI Advisor (AI Tư vấn)

### ⚡ Dự đoán tiền điện (Electricity Cost Prediction)

**Computed locally (no AI needed)** — this is deterministic math:

1. Query `RoomSession` for all finished sessions in the period (week/month)
2. For each session, calculate hours that overlap with each electricity rate tier
3. Sum: `equipment_total_kW × hours_in_tier × rate_per_kWh` for each tier
4. Display breakdown by tier and total estimate

The orchestrator is NOT used for this — it's pure local calculation using Room DB data + Equipment + ElectricityRate tables.

### 📦 Gợi ý nhập hàng (Inventory Alerts)

Uses `gpt-oss-120b` via Groq:

```json
POST /chat/completions
{
  "model": "openai/gpt-oss-120b",
  "messages": [{
    "role": "system",
    "content": "Bạn là trợ lý quản lý quán karaoke. Phân tích dữ liệu bán hàng và đưa ra gợi ý nhập hàng bằng tiếng Việt. Trả lời ngắn gọn, thực tế."
  }, {
    "role": "user",
    "content": "Dữ liệu bán hàng 7 ngày qua:\n{SALES_DATA_JSON}\n\nHôm nay là {DAY_OF_WEEK}. Gợi ý nhập hàng cho tuần tới?"
    // SALES_DATA_JSON shape: [{"name": "Bia Tiger", "totalQty": 126, "dailyAvg": 18, "days": [{"date": "2026-03-14", "qty": 20}, ...]}]
  }],
  "temperature": 0.3
}
```

- Tracks item sell rate from invoice history (last 7/30 days)
- AI analyzes patterns and generates Vietnamese recommendations
- Displayed as a list of recommendations on the AI Tư vấn tab

### 📋 Tóm tắt cuối ngày (Daily Summary)

Uses `gpt-oss-120b` via Groq:

```json
POST /chat/completions
{
  "model": "openai/gpt-oss-120b",
  "messages": [{
    "role": "system",
    "content": "Bạn là trợ lý quản lý quán karaoke. Tóm tắt hoạt động kinh doanh trong ngày bằng tiếng Việt tự nhiên. Ngắn gọn, dễ hiểu, có so sánh với ngày trước nếu có dữ liệu."
  }, {
    "role": "user",
    "content": "Dữ liệu hôm nay:\n{TODAY_DATA_JSON}\n\nDữ liệu hôm qua:\n{YESTERDAY_DATA_JSON}\n\nTóm tắt hoạt động hôm nay."
    // TODAY/YESTERDAY_DATA_JSON shape: {"date": "2026-03-20", "totalRevenue": 3850000, "sessionCount": 12, "rooms": [{"room": 1, "sessions": 7, "revenue": 2100000}, ...], "topItems": [{"name": "Bia Tiger", "qty": 18}, ...]}
  }],
  "temperature": 0.5
}
```

- Auto-generated Vietnamese natural language summary
- Compares with previous day trends
- Displayed at top of AI Tư vấn tab

## Settings Screen

Accessible from ⚙️ gear icon on Home screen top-right.

- **Thiết bị phòng (Equipment per room):** list of equipment with name + wattage (kW). Add/edit/delete. Separate lists for Room 1 and Room 2. Stored in `Equipment` DB table.
- **Biểu giá điện (Electricity rates):** table with tier name, hours, rate/kWh. Pre-seeded with QĐ 1279 rates. Editable. Stored in `ElectricityRate` DB table.
- **Groq API Key:** display only (read-only, set at build time via BuildConfig)

## Currency & Formatting

- Currency: Vietnamese Dong (₫)
- Format: `₫X.XXX.XXX` (dot as thousands separator, no decimals)
- Time format: 24-hour (HH:mm)
- Date format: dd/MM/yyyy
- Timezone: Asia/Ho_Chi_Minh (ICT, UTC+7)
