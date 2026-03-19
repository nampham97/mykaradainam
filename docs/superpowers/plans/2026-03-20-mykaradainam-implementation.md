# MyKaraDainam Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a production-quality Android karaoke room management app with OCR/voice invoice capture, AI-powered insights, and premium Catppuccin Dark UI.

**Architecture:** Single-module Kotlin Android app using Jetpack Compose (Material 3) with Room DB for local storage and Groq API for AI features. MVVM pattern with Hilt DI. All monetary values in VND (Long). Vietnamese UI.

**Tech Stack:** Kotlin 2.3.10, Compose BOM 2025.08.00, Material 3, Room, Retrofit + OkHttp, Hilt, CameraX, Kotlin Coroutines + Flow, Navigation Compose, Custom Canvas donut charts.

**Design Spec:** `docs/superpowers/specs/2026-03-20-mykaradainam-design.md`

**UI/UX Design System:**
- Theme: Catppuccin Dark (dark bg `#1e1e2e`, pastel accents)
- Style: Vibrant block-based with smooth micro-interactions (150-300ms)
- Touch targets: minimum 48dp
- Animations: spring-based for interactive elements, tween for transitions
- Loading: skeleton shimmer screens
- Charts: custom Canvas donut, max 5-6 colors, labels
- Icons: Material Icons (no emojis in production UI)
- No continuous/decorative animations — only purposeful ones

---

## File Structure

```
app/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   └── java/com/mykaradainam/
│       ├── MyKaraDainamApp.kt
│       ├── MainActivity.kt
│       ├── di/
│       │   ├── DatabaseModule.kt
│       │   └── NetworkModule.kt
│       ├── navigation/
│       │   ├── Routes.kt
│       │   └── NavGraph.kt
│       ├── ui/
│       │   ├── theme/
│       │   │   ├── Color.kt
│       │   │   ├── Type.kt
│       │   │   ├── Shape.kt
│       │   │   └── Theme.kt
│       │   ├── components/
│       │   │   ├── RoomCard.kt
│       │   │   ├── DonutChart.kt
│       │   │   ├── TimerDisplay.kt
│       │   │   ├── StatusBadge.kt
│       │   │   ├── LoadingButton.kt
│       │   │   └── ShimmerEffect.kt
│       │   ├── home/
│       │   │   ├── HomeScreen.kt
│       │   │   └── HomeViewModel.kt
│       │   ├── invoice/
│       │   │   ├── CameraScreen.kt
│       │   │   ├── CameraViewModel.kt
│       │   │   ├── VoiceScreen.kt
│       │   │   ├── VoiceViewModel.kt
│       │   │   ├── ConfirmScreen.kt
│       │   │   └── ConfirmViewModel.kt
│       │   ├── reports/
│       │   │   ├── ReportsScreen.kt
│       │   │   ├── AiAdvisorTab.kt
│       │   │   └── ReportsViewModel.kt
│       │   └── settings/
│       │       ├── SettingsScreen.kt
│       │       └── SettingsViewModel.kt
│       ├── data/
│       │   ├── local/
│       │   │   ├── AppDatabase.kt
│       │   │   ├── dao/
│       │   │   │   ├── RoomSessionDao.kt
│       │   │   │   ├── InvoiceItemDao.kt
│       │   │   │   ├── EquipmentDao.kt
│       │   │   │   └── ElectricityRateDao.kt
│       │   │   └── entity/
│       │   │       ├── RoomSessionEntity.kt
│       │   │       ├── InvoiceItemEntity.kt
│       │   │       ├── EquipmentEntity.kt
│       │   │       └── ElectricityRateEntity.kt
│       │   ├── remote/
│       │   │   └── groq/
│       │   │       ├── GroqApiService.kt
│       │   │       └── GroqModels.kt
│       │   └── repository/
│       │       ├── SessionRepository.kt
│       │       ├── InvoiceRepository.kt
│       │       ├── GroqRepository.kt
│       │       └── ReportsRepository.kt
│       ├── model/
│       │   └── RoomStatus.kt
│       └── util/
│           ├── CurrencyFormatter.kt
│           ├── TimeFormatter.kt
│           └── ElectricityCalculator.kt
├── src/test/java/com/mykaradainam/
│   ├── util/
│   │   ├── CurrencyFormatterTest.kt
│   │   ├── TimeFormatterTest.kt
│   │   └── ElectricityCalculatorTest.kt
│   └── data/repository/
│       └── SessionRepositoryTest.kt
├── src/androidTest/java/com/mykaradainam/
│   └── data/local/
│       ├── RoomSessionDaoTest.kt
│       └── InvoiceItemDaoTest.kt
├── build.gradle.kts (project-level)
├── settings.gradle.kts
├── gradle.properties
└── local.properties (GROQ_API_KEY=your_key_here)
```

---

### Task 1: Project Scaffold & Gradle Configuration

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts` (project-level)
- Create: `app/build.gradle.kts`
- Create: `gradle.properties`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/mykaradainam/MyKaraDainamApp.kt`
- Create: `app/src/main/java/com/mykaradainam/MainActivity.kt`

- [ ] **Step 1: Create project-level settings.gradle.kts**

```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyKaraDainam"
include(":app")
```

- [ ] **Step 2: Create project-level build.gradle.kts**

```kotlin
// build.gradle.kts (root)
plugins {
    kotlin("android") version "2.3.10" apply false
    kotlin("plugin.compose") version "2.3.10" apply false
    kotlin("plugin.serialization") version "2.3.10" apply false
    id("com.android.application") version "8.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.56.1" apply false
    id("com.google.devtools.ksp") version "2.3.10-1.0.30" apply false
}
```

- [ ] **Step 3: Create gradle.properties**

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 4: Create app/build.gradle.kts with all dependencies**

```kotlin
// app/build.gradle.kts
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.mykaradainam"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mykaradainam"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GROQ_API_KEY", "\"${localProperties.getProperty("GROQ_API_KEY", "")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2025.08.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.animation:animation")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Activity & Lifecycle
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0")

    // Navigation + Serialization (for type-safe routes)
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.56.1")
    ksp("com.google.dagger:hilt-android-compiler:2.56.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    ksp("androidx.room:room-compiler:2.7.1")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.5.0")
    implementation("androidx.camera:camera-camera2:1.5.0")
    implementation("androidx.camera:camera-lifecycle:1.5.0")
    implementation("androidx.camera:camera-view:1.5.0")

    // Gson
    implementation("com.google.code.gson:gson:2.11.0")

    // Core
    implementation("androidx.core:core-ktx:1.16.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.room:room-testing:2.7.1")
}
```

- [ ] **Step 5: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.INTERNET" />

    <uses-feature android:name="android.hardware.camera" android:required="false" />
    <uses-feature android:name="android.hardware.microphone" android:required="false" />

    <application
        android:name=".MyKaraDainamApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="MyKaraDainam"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyKaraDainam">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.MyKaraDainam">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 6: Create Application class with Hilt**

```kotlin
// MyKaraDainamApp.kt
package com.mykaradainam

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyKaraDainamApp : Application()
```

- [ ] **Step 7: Create MainActivity (placeholder)**

```kotlin
// MainActivity.kt
package com.mykaradainam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mykaradainam.ui.theme.MyKaraDainamTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyKaraDainamTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // NavGraph will be added in Task 9
                }
            }
        }
    }
}
```

- [ ] **Step 8: Create local.properties template**

Add to `local.properties`:
```properties
GROQ_API_KEY=your_groq_api_key_here
```

- [ ] **Step 9: Create res/values/themes.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.MyKaraDainam" parent="android:Theme.Material.NoActionBar">
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
    </style>
</resources>
```

- [ ] **Step 10: Verify project builds**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 11: Initialize git repo and commit**

```bash
git init
echo -e "*.iml\n.gradle\n/local.properties\n/.idea\n.DS_Store\n/build\n/captures\n.externalNativeBuild\n.cxx\nlocal.properties\n.superpowers/" > .gitignore
git add .
git commit -m "feat: scaffold Android project with Kotlin 2.3.10, Compose BOM 2025.08.00, Hilt, Room, Retrofit, CameraX"
```

---

### Task 2: Theme & Design System (Catppuccin Dark)

**Files:**
- Create: `app/src/main/java/com/mykaradainam/ui/theme/Color.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/theme/Type.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/theme/Shape.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/theme/Theme.kt`

- [ ] **Step 1: Create Color.kt — Catppuccin Mocha palette**

```kotlin
// Color.kt
package com.mykaradainam.ui.theme

import androidx.compose.ui.graphics.Color

// Catppuccin Mocha palette
object CatppuccinMocha {
    val Base = Color(0xFF1E1E2E)
    val Mantle = Color(0xFF181825)
    val Crust = Color(0xFF11111B)
    val Surface0 = Color(0xFF313244)
    val Surface1 = Color(0xFF45475A)
    val Surface2 = Color(0xFF585B70)
    val Overlay0 = Color(0xFF6C7086)
    val Overlay1 = Color(0xFF7F849C)
    val Overlay2 = Color(0xFF9399B2)
    val Subtext0 = Color(0xFFA6ADC8)
    val Subtext1 = Color(0xFFBAC2DE)
    val Text = Color(0xFFCDD6F4)
    val Lavender = Color(0xFFB4BEFE)
    val Blue = Color(0xFF89B4FA)
    val Sapphire = Color(0xFF74C7EC)
    val Sky = Color(0xFF89DCEB)
    val Teal = Color(0xFF94E2D5)
    val Green = Color(0xFFA6E3A1)
    val Yellow = Color(0xFFF9E2AF)
    val Peach = Color(0xFFFAB387)
    val Maroon = Color(0xFFEBA0AC)
    val Red = Color(0xFFF38BA8)
    val Mauve = Color(0xFFCBA6F7)
    val Pink = Color(0xFFF5C2E7)
    val Flamingo = Color(0xFFF2CDCD)
    val Rosewater = Color(0xFFF5E0DC)
}

// Semantic colors for the app
object AppColors {
    val RoomActive = CatppuccinMocha.Green
    val RoomFree = CatppuccinMocha.Surface1
    val TimerRunning = CatppuccinMocha.Red
    val CameraAccent = CatppuccinMocha.Blue
    val VoiceAccent = CatppuccinMocha.Mauve
    val ReportsAccent = CatppuccinMocha.Yellow
    val ConfirmAccent = CatppuccinMocha.Yellow
    val SettingsAccent = CatppuccinMocha.Overlay0
    val Revenue = CatppuccinMocha.Green
    val Room1Color = CatppuccinMocha.Blue
    val Room2Color = CatppuccinMocha.Mauve

    // Donut chart palette (max 6 colors)
    val ChartPalette = listOf(
        CatppuccinMocha.Red,
        CatppuccinMocha.Green,
        CatppuccinMocha.Yellow,
        CatppuccinMocha.Blue,
        CatppuccinMocha.Mauve,
        CatppuccinMocha.Peach
    )

    // Shimmer
    val ShimmerBase = CatppuccinMocha.Surface0
    val ShimmerHighlight = CatppuccinMocha.Surface1
}
```

- [ ] **Step 2: Create Type.kt — Typography**

```kotlin
// Type.kt
package com.mykaradainam.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Monospace for timers and numbers
val TimerTypography = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 34.sp,
    letterSpacing = 2.sp
)

val LargeAmountTypography = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 34.sp
)
```

- [ ] **Step 3: Create Shape.kt**

```kotlin
// Shape.kt
package com.mykaradainam.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
```

- [ ] **Step 4: Create Theme.kt — Material 3 dark theme with Catppuccin**

```kotlin
// Theme.kt
package com.mykaradainam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CatppuccinDarkScheme = darkColorScheme(
    primary = CatppuccinMocha.Mauve,
    onPrimary = CatppuccinMocha.Crust,
    primaryContainer = CatppuccinMocha.Mauve.copy(alpha = 0.2f),
    onPrimaryContainer = CatppuccinMocha.Mauve,
    secondary = CatppuccinMocha.Blue,
    onSecondary = CatppuccinMocha.Crust,
    secondaryContainer = CatppuccinMocha.Blue.copy(alpha = 0.2f),
    onSecondaryContainer = CatppuccinMocha.Blue,
    tertiary = CatppuccinMocha.Green,
    onTertiary = CatppuccinMocha.Crust,
    tertiaryContainer = CatppuccinMocha.Green.copy(alpha = 0.2f),
    onTertiaryContainer = CatppuccinMocha.Green,
    error = CatppuccinMocha.Red,
    onError = CatppuccinMocha.Crust,
    errorContainer = CatppuccinMocha.Red.copy(alpha = 0.2f),
    onErrorContainer = CatppuccinMocha.Red,
    background = CatppuccinMocha.Base,
    onBackground = CatppuccinMocha.Text,
    surface = CatppuccinMocha.Base,
    onSurface = CatppuccinMocha.Text,
    surfaceVariant = CatppuccinMocha.Surface0,
    onSurfaceVariant = CatppuccinMocha.Subtext1,
    outline = CatppuccinMocha.Surface2,
    outlineVariant = CatppuccinMocha.Surface1,
    inverseSurface = CatppuccinMocha.Text,
    inverseOnSurface = CatppuccinMocha.Base,
    surfaceContainerLowest = CatppuccinMocha.Crust,
    surfaceContainerLow = CatppuccinMocha.Mantle,
    surfaceContainer = CatppuccinMocha.Surface0,
    surfaceContainerHigh = CatppuccinMocha.Surface1,
    surfaceContainerHighest = CatppuccinMocha.Surface2
)

@Composable
fun MyKaraDainamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CatppuccinDarkScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
```

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/theme/
git commit -m "feat: add Catppuccin Dark theme with Material 3 color scheme, typography, shapes"
```

---

### Task 3: Data Model & Room Database

**Files:**
- Create: `app/src/main/java/com/mykaradainam/model/RoomStatus.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/entity/RoomSessionEntity.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/entity/InvoiceItemEntity.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/entity/EquipmentEntity.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/entity/ElectricityRateEntity.kt`

- [ ] **Step 1: Create RoomStatus enum**

```kotlin
// model/RoomStatus.kt
package com.mykaradainam.model

enum class RoomStatus {
    FREE,
    ACTIVE,
    INVOICED,
    FINISHED
}
```

- [ ] **Step 2: Create RoomSessionEntity**

```kotlin
// data/local/entity/RoomSessionEntity.kt
package com.mykaradainam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_sessions")
data class RoomSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomNumber: Int,
    val startTime: Long,
    val endTime: Long? = null,
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis()
)
```

- [ ] **Step 3: Create InvoiceItemEntity**

```kotlin
// data/local/entity/InvoiceItemEntity.kt
package com.mykaradainam.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = RoomSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val name: String,
    val quantity: Int,
    val unitPrice: Long,
    val subtotal: Long
)
```

- [ ] **Step 4: Create EquipmentEntity**

```kotlin
// data/local/entity/EquipmentEntity.kt
package com.mykaradainam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomNumber: Int,
    val name: String,
    val powerKw: Double
)
```

- [ ] **Step 5: Create ElectricityRateEntity**

```kotlin
// data/local/entity/ElectricityRateEntity.kt
package com.mykaradainam.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "electricity_rates")
data class ElectricityRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tierName: String,
    val startHour: Int,
    val endHour: Int,
    val ratePerKwh: Double
)
```

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/mykaradainam/model/ app/src/main/java/com/mykaradainam/data/local/entity/
git commit -m "feat: add Room entities for sessions, invoice items, equipment, electricity rates"
```

---

### Task 4: DAOs & Database

**Files:**
- Create: `app/src/main/java/com/mykaradainam/data/local/dao/RoomSessionDao.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/dao/InvoiceItemDao.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/dao/EquipmentDao.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/dao/ElectricityRateDao.kt`
- Create: `app/src/main/java/com/mykaradainam/data/local/AppDatabase.kt`
- Create: `app/src/main/java/com/mykaradainam/di/DatabaseModule.kt`

- [ ] **Step 1: Create RoomSessionDao**

```kotlin
// data/local/dao/RoomSessionDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.RoomSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomSessionDao {
    @Insert
    suspend fun insert(session: RoomSessionEntity): Long

    @Update
    suspend fun update(session: RoomSessionEntity)

    @Query("SELECT * FROM room_sessions WHERE status IN ('ACTIVE', 'INVOICED') AND roomNumber = :roomNumber LIMIT 1")
    suspend fun getActiveSession(roomNumber: Int): RoomSessionEntity?

    @Query("SELECT * FROM room_sessions WHERE status IN ('ACTIVE', 'INVOICED')")
    fun observeActiveSessions(): Flow<List<RoomSessionEntity>>

    @Query("SELECT * FROM room_sessions WHERE status IN ('ACTIVE', 'INVOICED')")
    suspend fun getActiveSessions(): List<RoomSessionEntity>

    @Query("SELECT * FROM room_sessions WHERE id = :id")
    suspend fun getById(id: Long): RoomSessionEntity?

    @Query("""
        SELECT * FROM room_sessions
        WHERE status = 'FINISHED'
        AND createdAt >= :startEpoch AND createdAt < :endEpoch
        ORDER BY createdAt DESC
    """)
    suspend fun getFinishedSessions(startEpoch: Long, endEpoch: Long): List<RoomSessionEntity>

    @Query("""
        SELECT roomNumber, COUNT(*) as sessionCount,
               AVG(endTime - startTime) as avgDuration,
               SUM(endTime - startTime) as totalTime
        FROM room_sessions
        WHERE status = 'FINISHED' AND createdAt >= :startEpoch AND createdAt < :endEpoch
        GROUP BY roomNumber
    """)
    suspend fun getRoomStats(startEpoch: Long, endEpoch: Long): List<RoomStatResult>

    @Query("SELECT COUNT(*) FROM room_sessions WHERE status = 'FINISHED' AND createdAt >= :startEpoch AND createdAt < :endEpoch")
    suspend fun getSessionCount(startEpoch: Long, endEpoch: Long): Int
}

data class RoomStatResult(
    val roomNumber: Int,
    val sessionCount: Int,
    val avgDuration: Long?,
    val totalTime: Long?
)
```

- [ ] **Step 2: Create InvoiceItemDao**

```kotlin
// data/local/dao/InvoiceItemDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceItemDao {
    @Insert
    suspend fun insertAll(items: List<InvoiceItemEntity>)

    @Query("SELECT * FROM invoice_items WHERE sessionId = :sessionId")
    suspend fun getBySessionId(sessionId: Long): List<InvoiceItemEntity>

    @Query("""
        SELECT SUM(subtotal) FROM invoice_items i
        JOIN room_sessions s ON i.sessionId = s.id
        WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
    """)
    suspend fun getTotalRevenue(startEpoch: Long, endEpoch: Long): Long?

    @Query("""
        SELECT s.roomNumber, SUM(i.subtotal) as revenue
        FROM invoice_items i
        JOIN room_sessions s ON i.sessionId = s.id
        WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
        GROUP BY s.roomNumber
    """)
    suspend fun getRevenueByRoom(startEpoch: Long, endEpoch: Long): List<RoomRevenueResult>

    @Query("""
        SELECT i.name, SUM(i.quantity) as totalQty, SUM(i.subtotal) as totalRevenue
        FROM invoice_items i
        JOIN room_sessions s ON i.sessionId = s.id
        WHERE s.createdAt >= :startEpoch AND s.createdAt < :endEpoch
        GROUP BY i.name ORDER BY totalQty DESC LIMIT 10
    """)
    suspend fun getTopSellingItems(startEpoch: Long, endEpoch: Long): List<TopItemResult>

    @Query("""
        SELECT SUM(subtotal) FROM invoice_items WHERE sessionId = :sessionId
    """)
    suspend fun getSessionTotal(sessionId: Long): Long?

    @Query("SELECT * FROM invoice_items WHERE sessionId = :sessionId")
    fun observeBySessionId(sessionId: Long): Flow<List<InvoiceItemEntity>>
}

data class RoomRevenueResult(
    val roomNumber: Int,
    val revenue: Long
)

data class TopItemResult(
    val name: String,
    val totalQty: Int,
    val totalRevenue: Long
)
```

- [ ] **Step 3: Create EquipmentDao**

```kotlin
// data/local/dao/EquipmentDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.EquipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Insert
    suspend fun insert(equipment: EquipmentEntity): Long

    @Update
    suspend fun update(equipment: EquipmentEntity)

    @Delete
    suspend fun delete(equipment: EquipmentEntity)

    @Query("SELECT * FROM equipment WHERE roomNumber = :roomNumber ORDER BY name")
    fun observeByRoom(roomNumber: Int): Flow<List<EquipmentEntity>>

    @Query("SELECT * FROM equipment WHERE roomNumber = :roomNumber")
    suspend fun getByRoom(roomNumber: Int): List<EquipmentEntity>

    @Query("SELECT SUM(powerKw) FROM equipment WHERE roomNumber = :roomNumber")
    suspend fun getTotalPowerKw(roomNumber: Int): Double?
}
```

- [ ] **Step 4: Create ElectricityRateDao**

```kotlin
// data/local/dao/ElectricityRateDao.kt
package com.mykaradainam.data.local.dao

import androidx.room.*
import com.mykaradainam.data.local.entity.ElectricityRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ElectricityRateDao {
    @Insert
    suspend fun insertAll(rates: List<ElectricityRateEntity>)

    @Update
    suspend fun update(rate: ElectricityRateEntity)

    @Query("SELECT * FROM electricity_rates ORDER BY startHour")
    fun observeAll(): Flow<List<ElectricityRateEntity>>

    @Query("SELECT * FROM electricity_rates ORDER BY startHour")
    suspend fun getAll(): List<ElectricityRateEntity>

    @Query("SELECT COUNT(*) FROM electricity_rates")
    suspend fun count(): Int
}
```

- [ ] **Step 5: Create AppDatabase with pre-seeded data**

```kotlin
// data/local/AppDatabase.kt
package com.mykaradainam.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mykaradainam.data.local.dao.*
import com.mykaradainam.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoomSessionEntity::class,
        InvoiceItemEntity::class,
        EquipmentEntity::class,
        ElectricityRateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomSessionDao(): RoomSessionDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun electricityRateDao(): ElectricityRateDao

    companion object {
        fun createCallback() = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-seed electricity rates (QĐ 1279/QĐ-BCT)
                db.execSQL("""
                    INSERT INTO electricity_rates (tierName, startHour, endHour, ratePerKwh) VALUES
                    ('Bình thường', 4, 17, 3152.0),
                    ('Cao điểm', 17, 20, 5422.0),
                    ('Bình thường', 20, 22, 3152.0),
                    ('Thấp điểm', 22, 4, 1918.0)
                """)
            }
        }
    }
}
```

- [ ] **Step 6: Create DatabaseModule (Hilt)**

```kotlin
// di/DatabaseModule.kt
package com.mykaradainam.di

import android.content.Context
import androidx.room.Room
import com.mykaradainam.data.local.AppDatabase
import com.mykaradainam.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mykaradainam.db"
        )
            .addCallback(AppDatabase.createCallback())
            .build()
    }

    @Provides fun provideRoomSessionDao(db: AppDatabase): RoomSessionDao = db.roomSessionDao()
    @Provides fun provideInvoiceItemDao(db: AppDatabase): InvoiceItemDao = db.invoiceItemDao()
    @Provides fun provideEquipmentDao(db: AppDatabase): EquipmentDao = db.equipmentDao()
    @Provides fun provideElectricityRateDao(db: AppDatabase): ElectricityRateDao = db.electricityRateDao()
}
```

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/mykaradainam/data/local/ app/src/main/java/com/mykaradainam/di/DatabaseModule.kt
git commit -m "feat: add Room database with DAOs, entities, pre-seeded electricity rates, Hilt DI"
```

---

### Task 5: Groq API Service

**Files:**
- Create: `app/src/main/java/com/mykaradainam/data/remote/groq/GroqModels.kt`
- Create: `app/src/main/java/com/mykaradainam/data/remote/groq/GroqApiService.kt`
- Create: `app/src/main/java/com/mykaradainam/di/NetworkModule.kt`

- [ ] **Step 1: Create GroqModels.kt — request/response models**

```kotlin
// data/remote/groq/GroqModels.kt
package com.mykaradainam.data.remote.groq

import com.google.gson.annotations.SerializedName

// Chat Completion
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double = 0.1
)

data class ChatMessage(
    val role: String,
    val content: Any // String or List<ContentPart>
)

data class ContentPart(
    val type: String,
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null
)

data class ImageUrl(val url: String)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)

// Whisper Transcription response
data class TranscriptionResponse(
    val text: String
)

// Parsed invoice from AI
data class InvoiceParseResult(
    val items: List<ParsedItem>,
    val totalAmount: Long,
    val confidence: String = "high",
    val warnings: List<String> = emptyList()
)

data class ParsedItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Long
)
```

- [ ] **Step 2: Create GroqApiService.kt — Retrofit interface**

```kotlin
// data/remote/groq/GroqApiService.kt
package com.mykaradainam.data.remote.groq

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface GroqApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse

    @Multipart
    @POST("audio/transcriptions")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language") language: RequestBody
    ): TranscriptionResponse
}
```

- [ ] **Step 3: Create NetworkModule.kt (Hilt)**

```kotlin
// di/NetworkModule.kt
package com.mykaradainam.di

import com.mykaradainam.BuildConfig
import com.mykaradainam.data.remote.groq.GroqApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                .build()
            chain.proceed(request)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGroqApiService(retrofit: Retrofit): GroqApiService {
        return retrofit.create(GroqApiService::class.java)
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/mykaradainam/data/remote/ app/src/main/java/com/mykaradainam/di/NetworkModule.kt
git commit -m "feat: add Groq API service with Retrofit, chat completions + audio transcription endpoints"
```

---

### Task 6: Repositories

**Files:**
- Create: `app/src/main/java/com/mykaradainam/data/repository/SessionRepository.kt`
- Create: `app/src/main/java/com/mykaradainam/data/repository/InvoiceRepository.kt`
- Create: `app/src/main/java/com/mykaradainam/data/repository/GroqRepository.kt`
- Create: `app/src/main/java/com/mykaradainam/data/repository/ReportsRepository.kt`

- [ ] **Step 1: Create SessionRepository**

```kotlin
// data/repository/SessionRepository.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.local.dao.RoomSessionDao
import com.mykaradainam.data.local.entity.RoomSessionEntity
import com.mykaradainam.model.RoomStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: RoomSessionDao
) {
    fun observeActiveSessions(): Flow<List<RoomSessionEntity>> =
        sessionDao.observeActiveSessions()

    suspend fun getActiveSessions(): List<RoomSessionEntity> =
        sessionDao.getActiveSessions()

    suspend fun getActiveSession(roomNumber: Int): RoomSessionEntity? =
        sessionDao.getActiveSession(roomNumber)

    suspend fun startSession(roomNumber: Int): Long {
        val existing = sessionDao.getActiveSession(roomNumber)
        if (existing != null) return existing.id

        val session = RoomSessionEntity(
            roomNumber = roomNumber,
            startTime = System.currentTimeMillis(),
            status = RoomStatus.ACTIVE.name
        )
        return sessionDao.insert(session)
    }

    suspend fun finishSession(roomNumber: Int) {
        val session = sessionDao.getActiveSession(roomNumber) ?: return
        sessionDao.update(
            session.copy(
                endTime = System.currentTimeMillis(),
                status = RoomStatus.FINISHED.name
            )
        )
    }

    suspend fun markInvoiced(sessionId: Long) {
        val session = sessionDao.getById(sessionId) ?: return
        if (session.status == RoomStatus.ACTIVE.name) {
            sessionDao.update(session.copy(status = RoomStatus.INVOICED.name))
        }
    }

    suspend fun getById(id: Long): RoomSessionEntity? = sessionDao.getById(id)
}
```

- [ ] **Step 2: Create InvoiceRepository**

```kotlin
// data/repository/InvoiceRepository.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.local.dao.InvoiceItemDao
import com.mykaradainam.data.local.entity.InvoiceItemEntity
import com.mykaradainam.data.remote.groq.ParsedItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepository @Inject constructor(
    private val invoiceItemDao: InvoiceItemDao
) {
    suspend fun saveInvoiceItems(sessionId: Long, items: List<ParsedItem>) {
        val entities = items.map { item ->
            InvoiceItemEntity(
                sessionId = sessionId,
                name = item.name,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                subtotal = item.quantity.toLong() * item.unitPrice
            )
        }
        invoiceItemDao.insertAll(entities)
    }

    suspend fun getSessionItems(sessionId: Long) =
        invoiceItemDao.getBySessionId(sessionId)

    suspend fun getSessionTotal(sessionId: Long): Long =
        invoiceItemDao.getSessionTotal(sessionId) ?: 0L
}
```

- [ ] **Step 3: Create GroqRepository**

```kotlin
// data/repository/GroqRepository.kt
package com.mykaradainam.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import com.mykaradainam.data.remote.groq.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqRepository @Inject constructor(
    private val api: GroqApiService,
    private val gson: Gson
) {
    suspend fun processInvoiceImage(bitmap: Bitmap): InvoiceParseResult {
        val base64 = bitmapToBase64(bitmap)
        val request = ChatRequest(
            model = "meta-llama/llama-4-scout-17b-16e-instruct",
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "Bạn là trợ lý đọc hóa đơn karaoke. Trích xuất thông tin từ ảnh hóa đơn và trả về JSON với format: {\"items\": [{\"name\": string, \"quantity\": int, \"unitPrice\": int}], \"totalAmount\": int, \"confidence\": \"high\"|\"medium\"|\"low\", \"warnings\": [string]}. Chỉ trả về JSON, không giải thích."
                ),
                ChatMessage(
                    role = "user",
                    content = listOf(
                        ContentPart(type = "text", text = "Đọc hóa đơn này:"),
                        ContentPart(
                            type = "image_url",
                            imageUrl = ImageUrl("data:image/jpeg;base64,$base64")
                        )
                    )
                )
            ),
            temperature = 0.1
        )

        val response = api.chatCompletion(request)
        val json = response.choices.first().message.content
            .trim().removePrefix("```json").removeSuffix("```").trim()
        return gson.fromJson(json, InvoiceParseResult::class.java)
    }

    suspend fun processVoiceAudio(audioFile: File): InvoiceParseResult {
        // Step 1: Whisper transcription
        val filePart = MultipartBody.Part.createFormData(
            "file", audioFile.name,
            audioFile.asRequestBody("audio/m4a".toMediaTypeOrNull())
        )
        val modelPart = "whisper-large-v3".toRequestBody("text/plain".toMediaTypeOrNull())
        val langPart = "vi".toRequestBody("text/plain".toMediaTypeOrNull())

        val transcription = api.transcribeAudio(filePart, modelPart, langPart)

        // Step 2: Orchestrator structures the text
        val request = ChatRequest(
            model = "openai/gpt-oss-120b",
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "Bạn là trợ lý xử lý hóa đơn karaoke. Nhận mô tả bằng giọng nói (tiếng Việt) về hóa đơn và trả về JSON: {\"items\": [{\"name\": string, \"quantity\": int, \"unitPrice\": int}], \"totalAmount\": int, \"confidence\": \"high\"|\"medium\"|\"low\", \"warnings\": [string]}. Nếu không rõ giá, đặt unitPrice=0 và thêm warning. Chỉ trả về JSON."
                ),
                ChatMessage(role = "user", content = transcription.text)
            ),
            temperature = 0.1
        )

        val response = api.chatCompletion(request)
        val json = response.choices.first().message.content
            .trim().removePrefix("```json").removeSuffix("```").trim()
        return gson.fromJson(json, InvoiceParseResult::class.java)
    }

    suspend fun getAiAdvisorResponse(systemPrompt: String, userPrompt: String): String {
        val request = ChatRequest(
            model = "openai/gpt-oss-120b",
            messages = listOf(
                ChatMessage(role = "system", content = systemPrompt),
                ChatMessage(role = "user", content = userPrompt)
            ),
            temperature = 0.3
        )
        val response = api.chatCompletion(request)
        return response.choices.first().message.content
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        // Resize to max 1280px on longest edge
        val maxDim = 1280
        val scale = if (bitmap.width > bitmap.height) {
            maxDim.toFloat() / bitmap.width
        } else {
            maxDim.toFloat() / bitmap.height
        }
        val resized = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else bitmap

        val stream = ByteArrayOutputStream()
        resized.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}
```

- [ ] **Step 4: Add Gson provider to NetworkModule**

Add to `di/NetworkModule.kt`:
```kotlin
@Provides
@Singleton
fun provideGson(): Gson = Gson()
```

- [ ] **Step 5: Create ReportsRepository**

```kotlin
// data/repository/ReportsRepository.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.local.dao.*
import com.mykaradainam.util.TimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

data class ReportData(
    val totalRevenue: Long,
    val sessionCount: Int,
    val revenueByRoom: List<RoomRevenueResult>,
    val topItems: List<TopItemResult>,
    val roomStats: List<RoomStatResult>
)

@Singleton
class ReportsRepository @Inject constructor(
    private val sessionDao: RoomSessionDao,
    private val invoiceItemDao: InvoiceItemDao
) {
    suspend fun getReport(startEpoch: Long, endEpoch: Long): ReportData {
        return ReportData(
            totalRevenue = invoiceItemDao.getTotalRevenue(startEpoch, endEpoch) ?: 0L,
            sessionCount = sessionDao.getSessionCount(startEpoch, endEpoch),
            revenueByRoom = invoiceItemDao.getRevenueByRoom(startEpoch, endEpoch),
            topItems = invoiceItemDao.getTopSellingItems(startEpoch, endEpoch),
            roomStats = sessionDao.getRoomStats(startEpoch, endEpoch)
        )
    }

    suspend fun getSalesDataJson(startEpoch: Long, endEpoch: Long): String {
        val items = invoiceItemDao.getTopSellingItems(startEpoch, endEpoch)
        val json = items.map { """{"name":"${it.name}","totalQty":${it.totalQty},"totalRevenue":${it.totalRevenue}}""" }
        return "[${json.joinToString(",")}]"
    }

    suspend fun getReportDataJson(startEpoch: Long, endEpoch: Long, dateLabel: String): String {
        val report = getReport(startEpoch, endEpoch)
        val rooms = report.revenueByRoom.map { """{"room":${it.roomNumber},"revenue":${it.revenue}}""" }
        val items = report.topItems.take(5).map { """{"name":"${it.name}","qty":${it.totalQty}}""" }
        return """{"date":"$dateLabel","totalRevenue":${report.totalRevenue},"sessionCount":${report.sessionCount},"rooms":[${rooms.joinToString(",")}],"topItems":[${items.joinToString(",")}]}"""
    }
}
```

- [ ] **Step 6: Create SharedInvoiceDataHolder — bridge Camera/Voice → Confirm**

```kotlin
// data/repository/SharedInvoiceDataHolder.kt
package com.mykaradainam.data.repository

import com.mykaradainam.data.remote.groq.InvoiceParseResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedInvoiceDataHolder @Inject constructor() {
    private var _result: InvoiceParseResult? = null

    fun set(result: InvoiceParseResult) { _result = result }
    fun get(): InvoiceParseResult? = _result
    fun clear() { _result = null }
}
```

This singleton is injected into CameraViewModel, VoiceViewModel (to set the result) and ConfirmViewModel (to get it).

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/mykaradainam/data/repository/
git commit -m "feat: add repositories for sessions, invoices, Groq AI, reports, and shared invoice data holder"
```

---

### Task 7: Utility Functions

**Files:**
- Create: `app/src/main/java/com/mykaradainam/util/CurrencyFormatter.kt`
- Create: `app/src/main/java/com/mykaradainam/util/TimeFormatter.kt`
- Create: `app/src/main/java/com/mykaradainam/util/ElectricityCalculator.kt`
- Create: `app/src/test/java/com/mykaradainam/util/CurrencyFormatterTest.kt`
- Create: `app/src/test/java/com/mykaradainam/util/TimeFormatterTest.kt`
- Create: `app/src/test/java/com/mykaradainam/util/ElectricityCalculatorTest.kt`

- [ ] **Step 1: Write CurrencyFormatter test**

```kotlin
// test/util/CurrencyFormatterTest.kt
package com.mykaradainam.util

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyFormatterTest {
    @Test
    fun `format zero`() = assertEquals("₫0", formatVnd(0))

    @Test
    fun `format thousands`() = assertEquals("₫25.000", formatVnd(25000))

    @Test
    fun `format millions`() = assertEquals("₫3.850.000", formatVnd(3850000))

    @Test
    fun `format short millions`() = assertEquals("₫3.9M", formatVndShort(3850000))

    @Test
    fun `format short thousands`() = assertEquals("₫25K", formatVndShort(25000))

    @Test
    fun `format short zero`() = assertEquals("₫0", formatVndShort(0))
}
```

- [ ] **Step 2: Implement CurrencyFormatter**

```kotlin
// util/CurrencyFormatter.kt
package com.mykaradainam.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val vndSymbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
    groupingSeparator = '.'
}
private val vndFormat = DecimalFormat("#,###", vndSymbols)

fun formatVnd(amount: Long): String = "₫${vndFormat.format(amount)}"

fun formatVndShort(amount: Long): String = when {
    amount >= 1_000_000 -> {
        val m = amount / 100_000.0
        val rounded = Math.round(m) / 10.0
        if (rounded == rounded.toLong().toDouble()) "₫${rounded.toLong()}M"
        else "₫${rounded}M"
    }
    amount >= 1_000 -> "₫${amount / 1000}K"
    else -> "₫$amount"
}
```

- [ ] **Step 3: Run CurrencyFormatter tests**

Run: `./gradlew test --tests "com.mykaradainam.util.CurrencyFormatterTest"`
Expected: ALL PASS

- [ ] **Step 4: Write TimeFormatter test**

```kotlin
// test/util/TimeFormatterTest.kt
package com.mykaradainam.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {
    @Test
    fun `format duration zero`() = assertEquals("00:00:00", formatDuration(0))

    @Test
    fun `format duration hours`() = assertEquals("01:23:45", formatDuration(5025000))

    @Test
    fun `format duration minutes only`() = assertEquals("00:05:30", formatDuration(330000))

    @Test
    fun `format short duration`() = assertEquals("1h 23m", formatDurationShort(5025000))

    @Test
    fun `format short minutes`() = assertEquals("5m", formatDurationShort(330000))
}
```

- [ ] **Step 5: Implement TimeFormatter**

```kotlin
// util/TimeFormatter.kt
package com.mykaradainam.util

import java.text.SimpleDateFormat
import java.util.*

private val ictZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun formatDurationShort(millis: Long): String {
    val totalMinutes = millis / 60000
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}

fun formatTime(epochMs: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale("vi", "VN")).apply {
        timeZone = ictZone
    }
    return sdf.format(Date(epochMs))
}

fun formatDate(epochMs: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN")).apply {
        timeZone = ictZone
    }
    return sdf.format(Date(epochMs))
}

fun todayStartEpoch(): Long {
    val cal = Calendar.getInstance(ictZone).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun todayEndEpoch(): Long = todayStartEpoch() + 86_400_000L

fun monthStartEpoch(): Long {
    val cal = Calendar.getInstance(ictZone).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

fun monthEndEpoch(): Long {
    val cal = Calendar.getInstance(ictZone).apply {
        add(Calendar.MONTH, 1)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}
```

- [ ] **Step 6: Run TimeFormatter tests**

Run: `./gradlew test --tests "com.mykaradainam.util.TimeFormatterTest"`
Expected: ALL PASS

- [ ] **Step 7: Write ElectricityCalculator test**

```kotlin
// test/util/ElectricityCalculatorTest.kt
package com.mykaradainam.util

import com.mykaradainam.data.local.entity.ElectricityRateEntity
import com.mykaradainam.data.local.entity.RoomSessionEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class ElectricityCalculatorTest {
    private val rates = listOf(
        ElectricityRateEntity(1, "Bình thường", 4, 17, 3152.0),
        ElectricityRateEntity(2, "Cao điểm", 17, 20, 5422.0),
        ElectricityRateEntity(3, "Bình thường", 20, 22, 3152.0),
        ElectricityRateEntity(4, "Thấp điểm", 22, 4, 1918.0)
    )

    @Test
    fun `calculate cost for session entirely in normal hours`() {
        // 14:00 - 16:00 = 2 hours normal rate
        val start = makeEpoch(14, 0)
        val end = makeEpoch(16, 0)
        val totalKw = 3.45 // total equipment kW
        val cost = calculateElectricityCost(start, end, totalKw, rates)
        // 3.45 kW * 2h * 3152 = 21,748.8
        assertEquals(21749, cost, 1.0)
    }

    @Test
    fun `calculate cost spanning peak hours`() {
        // 16:00 - 19:00 = 1h normal + 2h peak
        val start = makeEpoch(16, 0)
        val end = makeEpoch(19, 0)
        val totalKw = 3.45
        val cost = calculateElectricityCost(start, end, totalKw, rates)
        // 1h * 3152 * 3.45 + 2h * 5422 * 3.45 = 10,874.4 + 37,411.8 = 48,286.2
        assertEquals(48286, cost, 1.0)
    }

    private fun makeEpoch(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")).apply {
            set(2026, Calendar.MARCH, 20, hour, minute, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}
```

- [ ] **Step 8: Implement ElectricityCalculator**

```kotlin
// util/ElectricityCalculator.kt
package com.mykaradainam.util

import com.mykaradainam.data.local.entity.ElectricityRateEntity
import java.util.*

private val ictZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh")

fun calculateElectricityCost(
    startEpoch: Long,
    endEpoch: Long,
    totalKw: Double,
    rates: List<ElectricityRateEntity>
): Double {
    if (startEpoch >= endEpoch || totalKw <= 0.0 || rates.isEmpty()) return 0.0

    var totalCost = 0.0
    var current = startEpoch

    while (current < endEpoch) {
        val cal = Calendar.getInstance(ictZone).apply { timeInMillis = current }
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)

        val rate = findRate(currentHour, rates)
        val rateEndHour = rate?.endHour ?: (currentHour + 1)

        // Calculate time until rate tier changes or session ends
        val tierEndCal = Calendar.getInstance(ictZone).apply {
            timeInMillis = current
            set(Calendar.HOUR_OF_DAY, rateEndHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (rateEndHour <= currentHour) add(Calendar.DAY_OF_MONTH, 1)
        }

        val segmentEnd = minOf(tierEndCal.timeInMillis, endEpoch)
        val hours = (segmentEnd - current) / 3_600_000.0
        val ratePerKwh = rate?.ratePerKwh ?: 3152.0

        totalCost += totalKw * hours * ratePerKwh
        current = segmentEnd
    }

    return totalCost
}

private fun findRate(hour: Int, rates: List<ElectricityRateEntity>): ElectricityRateEntity? {
    return rates.find { rate ->
        if (rate.startHour < rate.endHour) {
            hour >= rate.startHour && hour < rate.endHour
        } else {
            // Wraps around midnight (e.g., 22-4)
            hour >= rate.startHour || hour < rate.endHour
        }
    }
}

suspend fun calculateTotalElectricityCost(
    sessions: List<Pair<Long, Long>>, // startEpoch, endEpoch pairs
    equipmentKwByRoom: Map<Int, Double>, // roomNumber -> total kW
    roomNumbers: List<Int>, // parallel to sessions
    rates: List<ElectricityRateEntity>
): Double {
    var total = 0.0
    sessions.forEachIndexed { index, (start, end) ->
        val roomNum = roomNumbers[index]
        val kw = equipmentKwByRoom[roomNum] ?: 0.0
        total += calculateElectricityCost(start, end, kw, rates)
    }
    return total
}
```

- [ ] **Step 9: Run ElectricityCalculator tests**

Run: `./gradlew test --tests "com.mykaradainam.util.ElectricityCalculatorTest"`
Expected: ALL PASS

- [ ] **Step 10: Commit**

```bash
git add app/src/main/java/com/mykaradainam/util/ app/src/test/
git commit -m "feat: add utility functions for VND formatting, time formatting, electricity calculation with tests"
```

---

### Task 8: Shared UI Components (Magic UI)

**Files:**
- Create: `app/src/main/java/com/mykaradainam/ui/components/ShimmerEffect.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/components/StatusBadge.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/components/TimerDisplay.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/components/RoomCard.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/components/DonutChart.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/components/LoadingButton.kt`

- [ ] **Step 1: Create ShimmerEffect — animated loading placeholder**

```kotlin
// ui/components/ShimmerEffect.kt
package com.mykaradainam.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mykaradainam.ui.theme.AppColors

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 20.dp,
    cornerRadius: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            AppColors.ShimmerBase,
            AppColors.ShimmerHighlight,
            AppColors.ShimmerBase
        ),
        start = Offset(translateX, 0f),
        end = Offset(translateX + 200f, 0f)
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
fun ShimmerCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(width = 120.dp, height = 16.dp)
        ShimmerBox(width = 200.dp, height = 32.dp)
        ShimmerBox(width = 160.dp, height = 12.dp)
    }
}
```

- [ ] **Step 2: Create StatusBadge — ACTIVE/FREE with glow**

```kotlin
// ui/components/StatusBadge.kt
package com.mykaradainam.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mykaradainam.model.RoomStatus
import com.mykaradainam.ui.theme.CatppuccinMocha

@Composable
fun StatusBadge(status: RoomStatus, modifier: Modifier = Modifier) {
    val isActive = status == RoomStatus.ACTIVE || status == RoomStatus.INVOICED
    val bgColor by animateColorAsState(
        targetValue = if (isActive) CatppuccinMocha.Green else CatppuccinMocha.Surface1,
        animationSpec = spring(),
        label = "badgeBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) CatppuccinMocha.Crust else CatppuccinMocha.Overlay0,
        animationSpec = spring(),
        label = "badgeText"
    )
    val label = when (status) {
        RoomStatus.ACTIVE, RoomStatus.INVOICED -> "ĐANG HÁT"
        RoomStatus.FREE -> "TRỐNG"
        RoomStatus.FINISHED -> "XONG"
    }

    Text(
        text = label,
        color = textColor,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = modifier
            .then(
                if (isActive) Modifier.shadow(8.dp, RoundedCornerShape(8.dp), spotColor = CatppuccinMocha.Green.copy(alpha = 0.4f))
                else Modifier
            )
            .background(bgColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
```

- [ ] **Step 3: Create TimerDisplay — animated live timer with glow**

```kotlin
// ui/components/TimerDisplay.kt
package com.mykaradainam.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatDuration
import kotlinx.coroutines.delay

@Composable
fun TimerDisplay(
    startTimeEpoch: Long,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp
) {
    var elapsed by remember { mutableLongStateOf(System.currentTimeMillis() - startTimeEpoch) }

    // Update every second
    LaunchedEffect(startTimeEpoch) {
        while (true) {
            elapsed = System.currentTimeMillis() - startTimeEpoch
            delay(1000)
        }
    }

    // Pulsing colon animation
    val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
    val colonAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colonAlpha"
    )

    val formatted = formatDuration(elapsed)
    val parts = formatted.split(":")

    Row(modifier = modifier) {
        Text(
            text = parts[0],
            color = CatppuccinMocha.Red,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
        Text(
            text = ":",
            color = CatppuccinMocha.Red,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.alpha(colonAlpha)
        )
        Text(
            text = parts[1],
            color = CatppuccinMocha.Red,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
        Text(
            text = ":",
            color = CatppuccinMocha.Red,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.alpha(colonAlpha)
        )
        Text(
            text = parts[2],
            color = CatppuccinMocha.Red,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
    }
}
```

- [ ] **Step 4: Create RoomCard — premium card with glow, spring animation on press**

```kotlin
// ui/components/RoomCard.kt
package com.mykaradainam.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mykaradainam.model.RoomStatus
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatTime

@Composable
fun RoomCard(
    roomNumber: Int,
    status: RoomStatus,
    startTime: Long?,
    modifier: Modifier = Modifier,
    onStart: () -> Unit = {},
    onFinish: () -> Unit = {},
    onInvoice: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "cardScale"
    )

    val isActive = status == RoomStatus.ACTIVE || status == RoomStatus.INVOICED
    val borderGradient = if (isActive) {
        Brush.linearGradient(listOf(CatppuccinMocha.Green.copy(alpha = 0.6f), CatppuccinMocha.Green.copy(alpha = 0.1f)))
    } else {
        Brush.linearGradient(listOf(CatppuccinMocha.Surface1, CatppuccinMocha.Surface1))
    }

    Column(
        modifier = modifier
            .scale(scale)
            .then(
                if (isActive) Modifier.shadow(12.dp, RoundedCornerShape(16.dp), spotColor = CatppuccinMocha.Green.copy(alpha = 0.2f))
                else Modifier
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(CatppuccinMocha.Surface0, CatppuccinMocha.Surface0.copy(alpha = 0.8f))
                )
            )
            .clickable(interactionSource = interactionSource, indication = null) {}
            .padding(14.dp)
            .animateContentSize(spring()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Phòng $roomNumber",
                color = CatppuccinMocha.Text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            StatusBadge(status)
        }

        // Timer or Available
        if (isActive && startTime != null) {
            TimerDisplay(startTimeEpoch = startTime, fontSize = 22.sp)
            Text(
                text = "Bắt đầu ${formatTime(startTime)}",
                color = CatppuccinMocha.Overlay0,
                fontSize = 10.sp
            )
        } else {
            Text(
                text = "Sẵn sàng",
                color = CatppuccinMocha.Overlay0,
                fontSize = 14.sp
            )
        }

        // Actions
        if (isActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onFinish,
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CatppuccinMocha.Red,
                        contentColor = CatppuccinMocha.Crust
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Kết thúc", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                OutlinedButton(
                    onClick = onInvoice,
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CatppuccinMocha.Text),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Hóa đơn", fontSize = 11.sp)
                }
            }
        } else {
            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CatppuccinMocha.Green,
                    contentColor = CatppuccinMocha.Crust
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Bắt đầu", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
```

- [ ] **Step 5: Create DonutChart — custom Canvas with animation**

```kotlin
// ui/components/DonutChart.kt
package com.mykaradainam.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mykaradainam.ui.theme.AppColors
import com.mykaradainam.ui.theme.CatppuccinMocha

data class DonutSlice(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun DonutChart(
    slices: List<DonutSlice>,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 14.dp,
    centerLabel: String = ""
) {
    val total = slices.sumOf { it.value.toDouble() }.toFloat()
    if (total == 0f) return

    // Animate in
    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "donutAnim"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = strokeWidth.toPx()
                val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
                val topLeft = Offset(stroke / 2, stroke / 2)

                var startAngle = -90f
                slices.forEach { slice ->
                    val sweep = (slice.value / total) * 360f * animationProgress
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    startAngle += sweep
                }
            }

            if (centerLabel.isNotEmpty()) {
                Text(
                    text = centerLabel,
                    color = CatppuccinMocha.Text,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Legend
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            slices.forEach { slice ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = slice.color)
                    }
                    Text(
                        text = slice.label,
                        color = CatppuccinMocha.Text,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
```

- [ ] **Step 6: Create LoadingButton — button with loading spinner**

```kotlin
// ui/components/LoadingButton.kt
package com.mykaradainam.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mykaradainam.ui.theme.CatppuccinMocha

@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = CatppuccinMocha.Mauve,
    contentColor: Color = CatppuccinMocha.Crust
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        AnimatedContent(targetState = isLoading, label = "btnLoading") { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = contentColor
                )
            } else {
                Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}
```

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/components/
git commit -m "feat: add shared UI components — ShimmerEffect, StatusBadge, TimerDisplay, RoomCard, DonutChart, LoadingButton"
```

---

### Task 9: Navigation

**Files:**
- Create: `app/src/main/java/com/mykaradainam/navigation/Routes.kt`
- Create: `app/src/main/java/com/mykaradainam/navigation/NavGraph.kt`
- Modify: `app/src/main/java/com/mykaradainam/MainActivity.kt`

- [ ] **Step 1: Create Routes.kt**

```kotlin
// navigation/Routes.kt
package com.mykaradainam.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data class Camera(val sessionId: Long, val roomNumber: Int) : Route
    @Serializable data class Voice(val sessionId: Long, val roomNumber: Int) : Route
    @Serializable data class Confirm(val sessionId: Long, val roomNumber: Int, val source: String) : Route
    @Serializable data object Reports : Route
    @Serializable data object Settings : Route
}
```

- [ ] **Step 2: Create NavGraph.kt (placeholder screens to be filled in later tasks)**

```kotlin
// navigation/NavGraph.kt
package com.mykaradainam.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mykaradainam.ui.home.HomeScreen
import com.mykaradainam.ui.invoice.CameraScreen
import com.mykaradainam.ui.invoice.VoiceScreen
import com.mykaradainam.ui.invoice.ConfirmScreen
import com.mykaradainam.ui.reports.ReportsScreen
import com.mykaradainam.ui.settings.SettingsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Home,
        enterTransition = { fadeIn(tween(200)) + slideInHorizontally { it / 4 } },
        exitTransition = { fadeOut(tween(200)) },
        popEnterTransition = { fadeIn(tween(200)) + slideInHorizontally { -it / 4 } },
        popExitTransition = { fadeOut(tween(200)) }
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToCamera = { sessionId, roomNum ->
                    navController.navigate(Route.Camera(sessionId, roomNum))
                },
                onNavigateToVoice = { sessionId, roomNum ->
                    navController.navigate(Route.Voice(sessionId, roomNum))
                },
                onNavigateToReports = {
                    navController.navigate(Route.Reports)
                },
                onNavigateToSettings = {
                    navController.navigate(Route.Settings)
                }
            )
        }

        composable<Route.Camera> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Camera>()
            CameraScreen(
                sessionId = route.sessionId,
                roomNumber = route.roomNumber,
                onNavigateToConfirm = {
                    navController.navigate(Route.Confirm(route.sessionId, route.roomNumber, "camera")) {
                        popUpTo<Route.Camera> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Voice> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Voice>()
            VoiceScreen(
                sessionId = route.sessionId,
                roomNumber = route.roomNumber,
                onNavigateToConfirm = {
                    navController.navigate(Route.Confirm(route.sessionId, route.roomNumber, "voice")) {
                        popUpTo<Route.Voice> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Confirm> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.Confirm>()
            ConfirmScreen(
                sessionId = route.sessionId,
                roomNumber = route.roomNumber,
                onSaved = {
                    navController.navigate(Route.Home) {
                        popUpTo<Route.Home> { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Reports> {
            ReportsScreen(onBack = { navController.popBackStack() })
        }

        composable<Route.Settings> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
```

- [ ] **Step 3: Update MainActivity to use NavGraph**

Replace the Surface content in `MainActivity.kt`:
```kotlin
MyKaraDainamTheme {
    Surface(modifier = Modifier.fillMaxSize()) {
        NavGraph()
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/mykaradainam/navigation/ app/src/main/java/com/mykaradainam/MainActivity.kt
git commit -m "feat: add type-safe navigation with animated transitions"
```

---

### Task 10: Home Screen

**Files:**
- Create: `app/src/main/java/com/mykaradainam/ui/home/HomeViewModel.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/home/HomeScreen.kt`

- [ ] **Step 1: Create HomeViewModel**

```kotlin
// ui/home/HomeViewModel.kt
package com.mykaradainam.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.local.entity.RoomSessionEntity
import com.mykaradainam.data.repository.InvoiceRepository
import com.mykaradainam.data.repository.ReportsRepository
import com.mykaradainam.data.repository.SessionRepository
import com.mykaradainam.model.RoomStatus
import com.mykaradainam.util.todayStartEpoch
import com.mykaradainam.util.todayEndEpoch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomState(
    val roomNumber: Int,
    val status: RoomStatus = RoomStatus.FREE,
    val sessionId: Long? = null,
    val startTime: Long? = null
)

data class HomeUiState(
    val room1: RoomState = RoomState(1),
    val room2: RoomState = RoomState(2),
    val todayRevenue: Long = 0L,
    val todaySessionCount: Int = 0,
    val showRoomPicker: Boolean = false,
    val pendingAction: String? = null // "camera" or "voice"
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeSessions()
        loadTodayStats()
    }

    private fun observeSessions() {
        viewModelScope.launch {
            sessionRepository.observeActiveSessions().collect { sessions ->
                val room1Session = sessions.find { it.roomNumber == 1 }
                val room2Session = sessions.find { it.roomNumber == 2 }
                _uiState.update { state ->
                    state.copy(
                        room1 = roomStateFrom(1, room1Session),
                        room2 = roomStateFrom(2, room2Session)
                    )
                }
            }
        }
    }

    fun loadTodayStats() {
        viewModelScope.launch {
            val start = todayStartEpoch()
            val end = todayEndEpoch()
            val report = reportsRepository.getReport(start, end)
            _uiState.update {
                it.copy(
                    todayRevenue = report.totalRevenue,
                    todaySessionCount = report.sessionCount
                )
            }
        }
    }

    fun startRoom(roomNumber: Int) {
        viewModelScope.launch {
            sessionRepository.startSession(roomNumber)
        }
    }

    fun finishRoom(roomNumber: Int) {
        viewModelScope.launch {
            sessionRepository.finishSession(roomNumber)
            loadTodayStats()
        }
    }

    fun requestQuickAction(action: String) {
        val state = _uiState.value
        val activeSessions = listOfNotNull(
            state.room1.sessionId?.let { state.room1 }.takeIf { state.room1.status != RoomStatus.FREE },
            state.room2.sessionId?.let { state.room2 }.takeIf { state.room2.status != RoomStatus.FREE }
        )

        when (activeSessions.size) {
            0 -> { /* No active rooms — do nothing */ }
            1 -> {
                _uiState.update { it.copy(pendingAction = null) }
                // Will be handled by the screen via callback
            }
            2 -> _uiState.update { it.copy(showRoomPicker = true, pendingAction = action) }
        }
    }

    fun dismissRoomPicker() {
        _uiState.update { it.copy(showRoomPicker = false, pendingAction = null) }
    }

    fun getActiveRooms(): List<RoomState> {
        val state = _uiState.value
        return listOf(state.room1, state.room2).filter { it.status != RoomStatus.FREE }
    }

    private fun roomStateFrom(roomNumber: Int, session: RoomSessionEntity?): RoomState {
        return if (session != null) {
            RoomState(
                roomNumber = roomNumber,
                status = RoomStatus.valueOf(session.status),
                sessionId = session.id,
                startTime = session.startTime
            )
        } else {
            RoomState(roomNumber = roomNumber)
        }
    }
}
```

- [ ] **Step 2: Create HomeScreen composable**

```kotlin
// ui/home/HomeScreen.kt
package com.mykaradainam.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.model.RoomStatus
import com.mykaradainam.ui.components.RoomCard
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatDate
import com.mykaradainam.util.formatVndShort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCamera: (sessionId: Long, roomNumber: Int) -> Unit,
    onNavigateToVoice: (sessionId: Long, roomNumber: Int) -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Room picker dialog
    if (state.showRoomPicker) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissRoomPicker() },
            title = { Text("Chọn phòng", color = CatppuccinMocha.Text) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.getActiveRooms().forEach { room ->
                        OutlinedButton(
                            onClick = {
                                viewModel.dismissRoomPicker()
                                val action = state.pendingAction
                                if (action == "camera") onNavigateToCamera(room.sessionId!!, room.roomNumber)
                                else onNavigateToVoice(room.sessionId!!, room.roomNumber)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Phòng ${room.roomNumber}")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.dismissRoomPicker() }) {
                    Text("Hủy")
                }
            },
            containerColor = CatppuccinMocha.Surface0
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "MyKaraDainam",
                            color = CatppuccinMocha.Mauve,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            formatDate(System.currentTimeMillis()),
                            color = CatppuccinMocha.Overlay0,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Cài đặt", tint = CatppuccinMocha.Overlay1)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatppuccinMocha.Base)
            )
        },
        containerColor = CatppuccinMocha.Base
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Room Cards side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RoomCard(
                    roomNumber = 1,
                    status = state.room1.status,
                    startTime = state.room1.startTime,
                    modifier = Modifier.weight(1f),
                    onStart = { viewModel.startRoom(1) },
                    onFinish = { viewModel.finishRoom(1) },
                    onInvoice = {
                        state.room1.sessionId?.let { sid ->
                            // Show method picker (camera/voice) - simplified: go to camera
                            onNavigateToCamera(sid, 1)
                        }
                    }
                )
                RoomCard(
                    roomNumber = 2,
                    status = state.room2.status,
                    startTime = state.room2.startTime,
                    modifier = Modifier.weight(1f),
                    onStart = { viewModel.startRoom(2) },
                    onFinish = { viewModel.finishRoom(2) },
                    onInvoice = {
                        state.room2.sessionId?.let { sid ->
                            onNavigateToCamera(sid, 2)
                        }
                    }
                )
            }

            // Quick Actions label
            Text(
                "Hành động nhanh",
                color = CatppuccinMocha.Overlay0,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )

            // Quick Action Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Camera
                QuickActionCard(
                    icon = Icons.Default.CameraAlt,
                    label = "Chụp hóa đơn",
                    color = CatppuccinMocha.Blue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val activeRooms = viewModel.getActiveRooms()
                        when (activeRooms.size) {
                            1 -> onNavigateToCamera(activeRooms[0].sessionId!!, activeRooms[0].roomNumber)
                            2 -> viewModel.requestQuickAction("camera")
                            else -> scope.launch { snackbarHostState.showSnackbar("Chưa có phòng hoạt động") }
                        }
                    }
                )
                // Voice
                QuickActionCard(
                    icon = Icons.Default.Mic,
                    label = "Nhập giọng nói",
                    color = CatppuccinMocha.Mauve,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val activeRooms = viewModel.getActiveRooms()
                        when (activeRooms.size) {
                            1 -> onNavigateToVoice(activeRooms[0].sessionId!!, activeRooms[0].roomNumber)
                            2 -> viewModel.requestQuickAction("voice")
                            else -> {}
                        }
                    }
                )
            }

            // Today Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CatppuccinMocha.Surface0)
                    .clickable { onNavigateToReports() }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    value = formatVndShort(state.todayRevenue),
                    label = "Hôm nay",
                    color = CatppuccinMocha.Green
                )
                Box(Modifier.width(1.dp).height(32.dp).background(CatppuccinMocha.Surface1))
                StatItem(
                    value = "${state.todaySessionCount}",
                    label = "Phiên",
                    color = CatppuccinMocha.Blue
                )
                Box(Modifier.width(1.dp).height(32.dp).background(CatppuccinMocha.Surface1))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        tint = CatppuccinMocha.Mauve,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Báo cáo", color = CatppuccinMocha.Overlay0, fontSize = 10.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CatppuccinMocha.Surface0)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatItem(value: String, label: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(label, color = CatppuccinMocha.Overlay0, fontSize = 10.sp)
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/home/
git commit -m "feat: add Home screen with side-by-side room cards, quick actions, today stats"
```

---

### Task 11: Camera & Voice Screens

**Files:**
- Create: `app/src/main/java/com/mykaradainam/ui/invoice/CameraViewModel.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/invoice/CameraScreen.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/invoice/VoiceViewModel.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/invoice/VoiceScreen.kt`

- [ ] **Step 1: Create CameraViewModel**

```kotlin
// ui/invoice/CameraViewModel.kt
package com.mykaradainam.ui.invoice

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.remote.groq.InvoiceParseResult
import com.mykaradainam.data.repository.GroqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraUiState(
    val capturedBitmap: Bitmap? = null,
    val isProcessing: Boolean = false,
    val parseResult: InvoiceParseResult? = null,
    val error: String? = null
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val groqRepository: GroqRepository,
    private val sharedInvoiceData: SharedInvoiceDataHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun onPhotoCaptured(bitmap: Bitmap) {
        _uiState.update { it.copy(capturedBitmap = bitmap) }
        processImage(bitmap)
    }

    private fun processImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            try {
                val result = groqRepository.processInvoiceImage(bitmap)
                sharedInvoiceData.set(result)
                _uiState.update { it.copy(isProcessing = false, parseResult = result) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isProcessing = false, error = "Không thể xử lý ảnh. Thử lại hoặc nhập thủ công.")
                }
            }
        }
    }

    fun retry() {
        _uiState.value.capturedBitmap?.let { processImage(it) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
```

- [ ] **Step 2: Create CameraScreen with CameraX**

```kotlin
// ui/invoice/CameraScreen.kt
package com.mykaradainam.ui.invoice

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.ui.components.LoadingButton
import com.mykaradainam.ui.theme.CatppuccinMocha
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    sessionId: Long,
    roomNumber: Int,
    onNavigateToConfirm: () -> Unit,
    onBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // Navigate when result is ready
    LaunchedEffect(state.parseResult) {
        if (state.parseResult != null) {
            onNavigateToConfirm()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chụp hóa đơn — Phòng $roomNumber", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatppuccinMocha.Base)
            )
        },
        containerColor = CatppuccinMocha.Base
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (!hasCameraPermission) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Cần quyền truy cập camera", color = CatppuccinMocha.Text)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { permissionLauncher.launch(android.Manifest.permission.CAMERA) }) {
                        Text("Cấp quyền")
                    }
                }
            } else if (state.capturedBitmap == null) {
                // Camera preview
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).also { previewView ->
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }
                                imageCapture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()

                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageCapture
                                )
                            }, ContextCompat.getMainExecutor(ctx))
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Capture button
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    IconButton(
                        onClick = {
                            imageCapture?.takePicture(
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageCapturedCallback() {
                                    override fun onCaptureSuccess(image: ImageProxy) {
                                        val bitmap = imageProxyToBitmap(image)
                                        image.close()
                                        viewModel.onPhotoCaptured(bitmap)
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .background(CatppuccinMocha.Blue, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Chụp",
                            tint = CatppuccinMocha.Crust,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            } else {
                // Show captured image + processing state
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.capturedBitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Ảnh hóa đơn",
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    if (state.isProcessing) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = CatppuccinMocha.Blue
                            )
                            Text("Đang xử lý...", color = CatppuccinMocha.Overlay1)
                        }
                    }

                    state.error?.let { error ->
                        Text(error, color = CatppuccinMocha.Red, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LoadingButton(
                                text = "Thử lại",
                                onClick = { viewModel.retry() },
                                containerColor = CatppuccinMocha.Blue
                            )
                            OutlinedButton(onClick = onNavigateToConfirm) {
                                Text("Nhập thủ công")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val rotation = image.imageInfo.rotationDegrees
    return if (rotation != 0) {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else bitmap
}
```

- [ ] **Step 3: Create VoiceViewModel**

```kotlin
// ui/invoice/VoiceViewModel.kt
package com.mykaradainam.ui.invoice

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.remote.groq.InvoiceParseResult
import com.mykaradainam.data.repository.GroqRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class VoiceUiState(
    val isRecording: Boolean = false,
    val isProcessing: Boolean = false,
    val recordingDurationMs: Long = 0L,
    val parseResult: InvoiceParseResult? = null,
    val error: String? = null
)

@HiltViewModel
class VoiceViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val groqRepository: GroqRepository,
    private val sharedInvoiceData: SharedInvoiceDataHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState.asStateFlow()

    private var recorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var recordingStartTime = 0L

    fun startRecording() {
        val file = File(context.cacheDir, "voice_${System.currentTimeMillis()}.m4a")
        audioFile = file

        recorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setMaxDuration(120_000) // 120s max
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }

        recordingStartTime = System.currentTimeMillis()
        _uiState.update { it.copy(isRecording = true) }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        _uiState.update { it.copy(isRecording = false) }
        processAudio()
    }

    private fun processAudio() {
        val file = audioFile ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            try {
                val result = groqRepository.processVoiceAudio(file)
                sharedInvoiceData.set(result)
                _uiState.update { it.copy(isProcessing = false, parseResult = result) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isProcessing = false, error = "Không thể xử lý giọng nói. Thử lại hoặc nhập thủ công.")
                }
            }
        }
    }

    fun retry() {
        processAudio()
    }

    override fun onCleared() {
        recorder?.release()
        audioFile?.delete()
    }
}
```

- [ ] **Step 4: Create VoiceScreen with animated recording UI**

```kotlin
// ui/invoice/VoiceScreen.kt
package com.mykaradainam.ui.invoice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.ui.components.LoadingButton
import com.mykaradainam.ui.components.TimerDisplay
import com.mykaradainam.ui.theme.CatppuccinMocha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceScreen(
    sessionId: Long,
    roomNumber: Int,
    onNavigateToConfirm: () -> Unit,
    onBack: () -> Unit,
    viewModel: VoiceViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var hasMicPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        hasMicPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!hasMicPermission) permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
    }

    LaunchedEffect(state.parseResult) {
        if (state.parseResult != null) onNavigateToConfirm()
    }

    // Pulsing animation for recording
    val infiniteTransition = rememberInfiniteTransition(label = "recordPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nhập giọng nói — Phòng $roomNumber", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatppuccinMocha.Base)
            )
        },
        containerColor = CatppuccinMocha.Base
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 4.dp,
                    color = CatppuccinMocha.Mauve
                )
                Spacer(Modifier.height(16.dp))
                Text("Đang xử lý giọng nói...", color = CatppuccinMocha.Overlay1)
            } else if (state.error != null) {
                Icon(Icons.Default.ErrorOutline, null, tint = CatppuccinMocha.Red, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text(state.error!!, color = CatppuccinMocha.Red, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LoadingButton("Thử lại", onClick = { viewModel.retry() }, containerColor = CatppuccinMocha.Mauve)
                    OutlinedButton(onClick = onNavigateToConfirm) { Text("Nhập thủ công") }
                }
            } else {
                // Recording UI
                Text(
                    if (state.isRecording) "Đang ghi âm..." else "Nhấn để ghi âm",
                    color = CatppuccinMocha.Text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Đọc thông tin hóa đơn: tên món, số lượng, giá",
                    color = CatppuccinMocha.Overlay0,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(48.dp))

                // Mic button with pulse
                IconButton(
                    onClick = {
                        if (state.isRecording) viewModel.stopRecording()
                        else viewModel.startRecording()
                    },
                    modifier = Modifier
                        .size(96.dp)
                        .then(if (state.isRecording) Modifier.scale(pulseScale) else Modifier)
                        .background(
                            if (state.isRecording) CatppuccinMocha.Red else CatppuccinMocha.Mauve,
                            CircleShape
                        )
                ) {
                    Icon(
                        if (state.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (state.isRecording) "Dừng" else "Ghi âm",
                        tint = CatppuccinMocha.Crust,
                        modifier = Modifier.size(40.dp)
                    )
                }

                if (state.isRecording) {
                    Spacer(Modifier.height(16.dp))
                    Text("Tối đa 2 phút", color = CatppuccinMocha.Overlay0, fontSize = 12.sp)
                }
            }
        }
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/invoice/
git commit -m "feat: add Camera OCR screen with CameraX and Voice input screen with animated recording UI"
```

---

### Task 12: Confirm Screen

**Files:**
- Create: `app/src/main/java/com/mykaradainam/ui/invoice/ConfirmViewModel.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/invoice/ConfirmScreen.kt`

- [ ] **Step 1: Create ConfirmViewModel**

```kotlin
// ui/invoice/ConfirmViewModel.kt
package com.mykaradainam.ui.invoice

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.remote.groq.InvoiceParseResult
import com.mykaradainam.data.remote.groq.ParsedItem
import com.mykaradainam.data.repository.InvoiceRepository
import com.mykaradainam.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditableItem(
    val name: String = "",
    val quantity: Int = 1,
    val unitPrice: Long = 0L
) {
    val subtotal: Long get() = quantity.toLong() * unitPrice
}

data class ConfirmUiState(
    val items: List<EditableItem> = emptyList(),
    val aiTotalAmount: Long = 0L,
    val confidence: String = "high",
    val warnings: List<String> = emptyList(),
    val isSaving: Boolean = false,
    val saved: Boolean = false
) {
    val computedTotal: Long get() = items.sumOf { it.subtotal }
    val hasMismatch: Boolean get() = aiTotalAmount > 0 && computedTotal != aiTotalAmount
}

@HiltViewModel
class ConfirmViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val sessionRepository: SessionRepository,
    private val sharedInvoiceData: SharedInvoiceDataHolder,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfirmUiState())
    val uiState: StateFlow<ConfirmUiState> = _uiState.asStateFlow()

    init {
        loadParseResult(sharedInvoiceData.get())
        sharedInvoiceData.clear()
    }

    fun loadParseResult(result: InvoiceParseResult?) {
        if (result != null) {
            _uiState.update {
                it.copy(
                    items = result.items.map { item ->
                        EditableItem(item.name, item.quantity, item.unitPrice)
                    },
                    aiTotalAmount = result.totalAmount,
                    confidence = result.confidence,
                    warnings = result.warnings
                )
            }
        } else {
            // Manual entry: start with one empty row
            _uiState.update { it.copy(items = listOf(EditableItem())) }
        }
    }

    fun updateItem(index: Int, item: EditableItem) {
        _uiState.update {
            val mutable = it.items.toMutableList()
            mutable[index] = item
            it.copy(items = mutable)
        }
    }

    fun addItem() {
        _uiState.update { it.copy(items = it.items + EditableItem()) }
    }

    fun removeItem(index: Int) {
        _uiState.update {
            val mutable = it.items.toMutableList()
            mutable.removeAt(index)
            it.copy(items = mutable)
        }
    }

    fun save(sessionId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val items = _uiState.value.items.filter { it.name.isNotBlank() }
            val parsedItems = items.map { ParsedItem(it.name, it.quantity, it.unitPrice) }
            invoiceRepository.saveInvoiceItems(sessionId, parsedItems)
            sessionRepository.markInvoiced(sessionId)
            _uiState.update { it.copy(isSaving = false, saved = true) }
        }
    }
}
```

- [ ] **Step 2: Create ConfirmScreen**

```kotlin
// ui/invoice/ConfirmScreen.kt
package com.mykaradainam.ui.invoice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.ui.components.LoadingButton
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatVnd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmScreen(
    sessionId: Long,
    roomNumber: Int,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: ConfirmViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xác nhận hóa đơn — Phòng $roomNumber", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatppuccinMocha.Base)
            )
        },
        containerColor = CatppuccinMocha.Base
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Warnings banner
            AnimatedVisibility(visible = state.confidence != "high" || state.warnings.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CatppuccinMocha.Yellow.copy(alpha = 0.15f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, null, tint = CatppuccinMocha.Yellow, modifier = Modifier.size(18.dp))
                    Text(
                        "AI không chắc chắn, vui lòng kiểm tra kỹ",
                        color = CatppuccinMocha.Yellow,
                        fontSize = 12.sp
                    )
                }
            }

            // Items list
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                itemsIndexed(state.items, key = { index, _ -> index }) { index, item ->
                    InvoiceItemRow(
                        item = item,
                        onUpdate = { viewModel.updateItem(index, it) },
                        onDelete = { viewModel.removeItem(index) }
                    )
                }

                item {
                    TextButton(
                        onClick = { viewModel.addItem() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Thêm mục")
                    }
                }
            }

            // Total + mismatch warning + save
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(CatppuccinMocha.Surface0)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(visible = state.hasMismatch) {
                    Text(
                        "Tổng AI (${formatVnd(state.aiTotalAmount)}) khác với tổng tính (${formatVnd(state.computedTotal)})",
                        color = CatppuccinMocha.Yellow,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tổng cộng", color = CatppuccinMocha.Overlay1, fontSize = 14.sp)
                    Text(
                        formatVnd(state.computedTotal),
                        color = CatppuccinMocha.Green,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LoadingButton(
                    text = "Lưu hóa đơn",
                    onClick = { viewModel.save(sessionId) },
                    isLoading = state.isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = CatppuccinMocha.Green,
                    contentColor = CatppuccinMocha.Crust
                )
            }
        }
    }
}

@Composable
private fun InvoiceItemRow(
    item: EditableItem,
    onUpdate: (EditableItem) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CatppuccinMocha.Surface0)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = item.name,
            onValueChange = { onUpdate(item.copy(name = it)) },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Tên", fontSize = 13.sp) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CatppuccinMocha.Surface1,
                focusedBorderColor = CatppuccinMocha.Mauve
            )
        )
        OutlinedTextField(
            value = if (item.quantity > 0) item.quantity.toString() else "",
            onValueChange = { onUpdate(item.copy(quantity = it.toIntOrNull() ?: 0)) },
            modifier = Modifier.width(48.dp),
            placeholder = { Text("SL", fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CatppuccinMocha.Surface1,
                focusedBorderColor = CatppuccinMocha.Mauve
            )
        )
        OutlinedTextField(
            value = if (item.unitPrice > 0) item.unitPrice.toString() else "",
            onValueChange = { onUpdate(item.copy(unitPrice = it.toLongOrNull() ?: 0)) },
            modifier = Modifier.width(80.dp),
            placeholder = { Text("Giá", fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = CatppuccinMocha.Surface1,
                focusedBorderColor = CatppuccinMocha.Mauve
            )
        )
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Close, null, tint = CatppuccinMocha.Red, modifier = Modifier.size(16.dp))
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/invoice/Confirm*
git commit -m "feat: add Confirm screen with editable invoice items, AI validation warnings, total calculation"
```

---

### Task 13: Reports Screen

**Files:**
- Create: `app/src/main/java/com/mykaradainam/ui/reports/ReportsViewModel.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/reports/ReportsScreen.kt`

- [ ] **Step 1: Create ReportsViewModel**

```kotlin
// ui/reports/ReportsViewModel.kt
package com.mykaradainam.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.local.dao.RoomRevenueResult
import com.mykaradainam.data.local.dao.RoomStatResult
import com.mykaradainam.data.local.dao.TopItemResult
import com.mykaradainam.data.repository.ReportData
import com.mykaradainam.data.repository.ReportsRepository
import com.mykaradainam.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportsUiState(
    val selectedTab: Int = 0, // 0=today, 1=month, 2=AI
    val isLoading: Boolean = true,
    val reportData: ReportData? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportsRepository: ReportsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReport(0)
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
        if (index < 2) loadReport(index)
    }

    private fun loadReport(tab: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val (start, end) = if (tab == 0) {
                todayStartEpoch() to todayEndEpoch()
            } else {
                monthStartEpoch() to monthEndEpoch()
            }
            val data = reportsRepository.getReport(start, end)
            _uiState.update { it.copy(isLoading = false, reportData = data) }
        }
    }
}
```

- [ ] **Step 2: Create ReportsScreen with donut charts**

```kotlin
// ui/reports/ReportsScreen.kt
package com.mykaradainam.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.ui.components.DonutChart
import com.mykaradainam.ui.components.DonutSlice
import com.mykaradainam.ui.components.ShimmerCard
import com.mykaradainam.ui.theme.AppColors
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("Hôm nay", "Tháng này", "AI Tư vấn")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Báo cáo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatppuccinMocha.Base)
            )
        },
        containerColor = CatppuccinMocha.Base
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Tabs
            PrimaryTabRow(
                selectedTabIndex = state.selectedTab,
                containerColor = CatppuccinMocha.Base,
                contentColor = CatppuccinMocha.Mauve
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(title, fontSize = 13.sp) }
                    )
                }
            }

            if (state.selectedTab < 2) {
                // Reports content
                if (state.isLoading) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(3) { ShimmerCard(Modifier.fillMaxWidth().height(100.dp)) }
                    }
                } else {
                    val data = state.reportData ?: return@Column

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Revenue card
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CatppuccinMocha.Surface0)
                                .padding(16.dp)
                        ) {
                            Text("Tổng doanh thu", color = CatppuccinMocha.Overlay0, fontSize = 11.sp, letterSpacing = 1.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(formatVnd(data.totalRevenue), color = CatppuccinMocha.Green, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("${data.sessionCount} phiên", color = CatppuccinMocha.Overlay0, fontSize = 12.sp)
                        }

                        // Revenue by room donut
                        if (data.revenueByRoom.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CatppuccinMocha.Surface0)
                                    .padding(16.dp)
                            ) {
                                Text("Doanh thu theo phòng", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                DonutChart(
                                    slices = data.revenueByRoom.map { r ->
                                        DonutSlice(
                                            label = "Phòng ${r.roomNumber}: ${formatVndShort(r.revenue)}",
                                            value = r.revenue.toFloat(),
                                            color = if (r.roomNumber == 1) AppColors.Room1Color else AppColors.Room2Color
                                        )
                                    },
                                    centerLabel = "2 phòng"
                                )
                            }
                        }

                        // Top items donut
                        if (data.topItems.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CatppuccinMocha.Surface0)
                                    .padding(16.dp)
                            ) {
                                Text("Mặt hàng bán chạy", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                DonutChart(
                                    slices = data.topItems.take(5).mapIndexed { i, item ->
                                        DonutSlice(
                                            label = "${item.name} (${item.totalQty})",
                                            value = item.totalQty.toFloat(),
                                            color = AppColors.ChartPalette[i % AppColors.ChartPalette.size]
                                        )
                                    },
                                    centerLabel = "Top ${minOf(data.topItems.size, 5)}"
                                )
                            }
                        }

                        // Room stats
                        if (data.roomStats.isNotEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CatppuccinMocha.Surface0)
                                    .padding(16.dp)
                            ) {
                                Text("Thống kê phòng", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(12.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    data.roomStats.forEach { stat ->
                                        Column(
                                            modifier = Modifier.weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(CatppuccinMocha.Base)
                                                .padding(12.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                "${stat.sessionCount}",
                                                color = if (stat.roomNumber == 1) CatppuccinMocha.Blue else CatppuccinMocha.Mauve,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("Phiên P${stat.roomNumber}", color = CatppuccinMocha.Overlay0, fontSize = 10.sp)
                                            stat.avgDuration?.let {
                                                Text("TB: ${formatDurationShort(it)}", color = CatppuccinMocha.Overlay0, fontSize = 9.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            } else {
                // AI Advisor tab - Task 14
                AiAdvisorTab()
            }
        }
    }
}
```

- [ ] **Step 3: Create placeholder AiAdvisorTab**

```kotlin
// ui/reports/AiAdvisorTab.kt
package com.mykaradainam.ui.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mykaradainam.ui.theme.CatppuccinMocha

@Composable
fun AiAdvisorTab() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text("AI Tư vấn — coming in Task 14", color = CatppuccinMocha.Overlay0)
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/reports/
git commit -m "feat: add Reports screen with revenue cards, animated donut charts, room stats"
```

---

### Task 14: AI Advisor Tab

**Files:**
- Modify: `app/src/main/java/com/mykaradainam/ui/reports/AiAdvisorTab.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/reports/AiAdvisorViewModel.kt`

- [ ] **Step 1: Create AiAdvisorViewModel**

```kotlin
// ui/reports/AiAdvisorViewModel.kt
package com.mykaradainam.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.local.dao.ElectricityRateDao
import com.mykaradainam.data.local.dao.EquipmentDao
import com.mykaradainam.data.local.dao.RoomSessionDao
import com.mykaradainam.data.repository.GroqRepository
import com.mykaradainam.data.repository.ReportsRepository
import com.mykaradainam.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class AiAdvisorUiState(
    val electricityCost: Double? = null,
    val electricityBreakdown: String = "",
    val inventoryAdvice: String? = null,
    val dailySummary: String? = null,
    val isLoadingElectricity: Boolean = false,
    val isLoadingInventory: Boolean = false,
    val isLoadingSummary: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AiAdvisorViewModel @Inject constructor(
    private val sessionDao: RoomSessionDao,
    private val equipmentDao: EquipmentDao,
    private val electricityRateDao: ElectricityRateDao,
    private val reportsRepository: ReportsRepository,
    private val groqRepository: GroqRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiAdvisorUiState())
    val uiState: StateFlow<AiAdvisorUiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    private fun loadAll() {
        calculateElectricity()
        loadInventoryAdvice()
        loadDailySummary()
    }

    private fun calculateElectricity() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingElectricity = true) }
            try {
                val rates = electricityRateDao.getAll()
                val kw1 = equipmentDao.getTotalPowerKw(1) ?: 0.0
                val kw2 = equipmentDao.getTotalPowerKw(2) ?: 0.0

                // Last 7 days
                val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
                cal.add(Calendar.DAY_OF_MONTH, -7)
                val weekStart = cal.timeInMillis
                val now = System.currentTimeMillis()

                val sessions = sessionDao.getFinishedSessions(weekStart, now)
                var totalCost = 0.0
                sessions.forEach { session ->
                    val endTime = session.endTime ?: return@forEach
                    val kw = if (session.roomNumber == 1) kw1 else kw2
                    totalCost += calculateElectricityCost(session.startTime, endTime, kw, rates)
                }

                val breakdown = "7 ngày qua: ${sessions.size} phiên\n" +
                    "Phòng 1: ${String.format("%.1f", kw1)} kW\n" +
                    "Phòng 2: ${String.format("%.1f", kw2)} kW\n" +
                    "Ước tính: ${formatVnd(totalCost.toLong())}"

                _uiState.update {
                    it.copy(isLoadingElectricity = false, electricityCost = totalCost, electricityBreakdown = breakdown)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingElectricity = false) }
            }
        }
    }

    private fun loadInventoryAdvice() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInventory = true) }
            try {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
                val now = cal.timeInMillis
                cal.add(Calendar.DAY_OF_MONTH, -7)
                val weekStart = cal.timeInMillis
                val salesData = reportsRepository.getSalesDataJson(weekStart, now)
                val dayOfWeek = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("vi", "VN")) ?: "Hôm nay"

                val response = groqRepository.getAiAdvisorResponse(
                    systemPrompt = "Bạn là trợ lý quản lý quán karaoke. Phân tích dữ liệu bán hàng và đưa ra gợi ý nhập hàng bằng tiếng Việt. Trả lời ngắn gọn, thực tế.",
                    userPrompt = "Dữ liệu bán hàng 7 ngày qua:\n$salesData\n\nHôm nay là $dayOfWeek. Gợi ý nhập hàng cho tuần tới?"
                )
                _uiState.update { it.copy(isLoadingInventory = false, inventoryAdvice = response) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingInventory = false, inventoryAdvice = "Không thể tải gợi ý. Kiểm tra kết nối mạng.") }
            }
        }
    }

    private fun loadDailySummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingSummary = true) }
            try {
                val todayData = reportsRepository.getReportDataJson(todayStartEpoch(), todayEndEpoch(), formatDate(System.currentTimeMillis()))
                val yesterdayStart = todayStartEpoch() - 86_400_000L
                val yesterdayData = reportsRepository.getReportDataJson(yesterdayStart, todayStartEpoch(), formatDate(yesterdayStart))

                val response = groqRepository.getAiAdvisorResponse(
                    systemPrompt = "Bạn là trợ lý quản lý quán karaoke. Tóm tắt hoạt động kinh doanh trong ngày bằng tiếng Việt tự nhiên. Ngắn gọn, dễ hiểu, có so sánh với ngày trước nếu có dữ liệu.",
                    userPrompt = "Dữ liệu hôm nay:\n$todayData\n\nDữ liệu hôm qua:\n$yesterdayData\n\nTóm tắt hoạt động hôm nay."
                )
                _uiState.update { it.copy(isLoadingSummary = false, dailySummary = response) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingSummary = false, dailySummary = "Không thể tải tóm tắt.") }
            }
        }
    }
}
```

- [ ] **Step 2: Update AiAdvisorTab with full UI**

Replace the placeholder `AiAdvisorTab.kt`:

```kotlin
// ui/reports/AiAdvisorTab.kt
package com.mykaradainam.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.ui.components.ShimmerCard
import com.mykaradainam.ui.theme.CatppuccinMocha
import com.mykaradainam.util.formatVnd

@Composable
fun AiAdvisorTab(viewModel: AiAdvisorViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Summary
        AdvisorCard(
            icon = Icons.Default.Summarize,
            title = "Tóm tắt hôm nay",
            color = CatppuccinMocha.Mauve,
            isLoading = state.isLoadingSummary,
            content = state.dailySummary
        )

        // Electricity Cost
        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CatppuccinMocha.Surface0)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.ElectricBolt, null, tint = CatppuccinMocha.Yellow, modifier = Modifier.size(20.dp))
                Text("Dự đoán tiền điện", color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            if (state.isLoadingElectricity) {
                ShimmerCard()
            } else if (state.electricityCost != null) {
                Text(
                    formatVnd(state.electricityCost!!.toLong()),
                    color = CatppuccinMocha.Yellow,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(state.electricityBreakdown, color = CatppuccinMocha.Overlay1, fontSize = 12.sp, lineHeight = 18.sp)
            } else {
                Text("Chưa có dữ liệu. Thêm thiết bị trong Cài đặt.", color = CatppuccinMocha.Overlay0, fontSize = 13.sp)
            }
        }

        // Inventory Advice
        AdvisorCard(
            icon = Icons.Default.Inventory,
            title = "Gợi ý nhập hàng",
            color = CatppuccinMocha.Green,
            isLoading = state.isLoadingInventory,
            content = state.inventoryAdvice
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AdvisorCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: androidx.compose.ui.graphics.Color,
    isLoading: Boolean,
    content: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CatppuccinMocha.Surface0)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Text(title, color = CatppuccinMocha.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
        if (isLoading) {
            ShimmerCard()
        } else {
            Text(
                content ?: "Chưa có dữ liệu.",
                color = CatppuccinMocha.Subtext1,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/reports/AiAdvisor*
git commit -m "feat: add AI Advisor tab with electricity prediction, inventory alerts, daily summary"
```

---

### Task 15: Settings Screen

**Files:**
- Create: `app/src/main/java/com/mykaradainam/ui/settings/SettingsViewModel.kt`
- Create: `app/src/main/java/com/mykaradainam/ui/settings/SettingsScreen.kt`

- [ ] **Step 1: Create SettingsViewModel**

```kotlin
// ui/settings/SettingsViewModel.kt
package com.mykaradainam.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mykaradainam.data.local.dao.ElectricityRateDao
import com.mykaradainam.data.local.dao.EquipmentDao
import com.mykaradainam.data.local.entity.ElectricityRateEntity
import com.mykaradainam.data.local.entity.EquipmentEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val room1Equipment: List<EquipmentEntity> = emptyList(),
    val room2Equipment: List<EquipmentEntity> = emptyList(),
    val rates: List<ElectricityRateEntity> = emptyList()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val equipmentDao: EquipmentDao,
    private val rateDao: ElectricityRateDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                equipmentDao.observeByRoom(1),
                equipmentDao.observeByRoom(2),
                rateDao.observeAll()
            ) { eq1, eq2, rates ->
                SettingsUiState(eq1, eq2, rates)
            }.collect { _uiState.value = it }
        }
    }

    fun addEquipment(roomNumber: Int, name: String, powerKw: Double) {
        viewModelScope.launch {
            equipmentDao.insert(EquipmentEntity(roomNumber = roomNumber, name = name, powerKw = powerKw))
        }
    }

    fun deleteEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch { equipmentDao.delete(equipment) }
    }

    fun updateRate(rate: ElectricityRateEntity) {
        viewModelScope.launch { rateDao.update(rate) }
    }
}
```

- [ ] **Step 2: Create SettingsScreen**

```kotlin
// ui/settings/SettingsScreen.kt
package com.mykaradainam.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mykaradainam.BuildConfig
import com.mykaradainam.ui.theme.CatppuccinMocha

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CatppuccinMocha.Base)
            )
        },
        containerColor = CatppuccinMocha.Base
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Equipment sections
            item {
                EquipmentSection(
                    title = "Thiết bị Phòng 1",
                    equipment = state.room1Equipment,
                    onAdd = { name, kw -> viewModel.addEquipment(1, name, kw) },
                    onDelete = { viewModel.deleteEquipment(it) }
                )
            }
            item {
                EquipmentSection(
                    title = "Thiết bị Phòng 2",
                    equipment = state.room2Equipment,
                    onAdd = { name, kw -> viewModel.addEquipment(2, name, kw) },
                    onDelete = { viewModel.deleteEquipment(it) }
                )
            }

            // Electricity rates
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CatppuccinMocha.Surface0)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Biểu giá điện", color = CatppuccinMocha.Text, fontWeight = FontWeight.SemiBold)
                    Text("QĐ 1279/QĐ-BCT — Hộ kinh doanh, dưới 6kV", color = CatppuccinMocha.Overlay0, fontSize = 11.sp)
                    state.rates.forEach { rate ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(rate.tierName, color = CatppuccinMocha.Text, fontSize = 13.sp)
                                Text("${rate.startHour}h - ${rate.endHour}h", color = CatppuccinMocha.Overlay0, fontSize = 11.sp)
                            }
                            Text("${rate.ratePerKwh.toLong()} đ/kWh", color = CatppuccinMocha.Yellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // API Key
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CatppuccinMocha.Surface0)
                        .padding(16.dp)
                ) {
                    Text("Groq API Key", color = CatppuccinMocha.Text, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    val key = BuildConfig.GROQ_API_KEY
                    Text(
                        if (key.length > 8) "${key.take(4)}...${key.takeLast(4)}" else "Chưa cấu hình",
                        color = CatppuccinMocha.Overlay0,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipmentSection(
    title: String,
    equipment: List<com.mykaradainam.data.local.entity.EquipmentEntity>,
    onAdd: (String, Double) -> Unit,
    onDelete: (com.mykaradainam.data.local.entity.EquipmentEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var kw by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thêm thiết bị") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên") }, singleLine = true)
                    OutlinedTextField(
                        value = kw, onValueChange = { kw = it },
                        label = { Text("Công suất (kW)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val power = kw.toDoubleOrNull() ?: return@TextButton
                    onAdd(name, power)
                    name = ""; kw = ""; showDialog = false
                }) { Text("Thêm") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Hủy") } },
            containerColor = CatppuccinMocha.Surface0
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CatppuccinMocha.Surface0)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = CatppuccinMocha.Text, fontWeight = FontWeight.SemiBold)
            IconButton(onClick = { showDialog = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, null, tint = CatppuccinMocha.Green, modifier = Modifier.size(18.dp))
            }
        }

        if (equipment.isEmpty()) {
            Text("Chưa có thiết bị", color = CatppuccinMocha.Overlay0, fontSize = 13.sp)
        }

        equipment.forEach { eq ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${eq.name} — ${eq.powerKw} kW", color = CatppuccinMocha.Subtext1, fontSize = 13.sp)
                IconButton(onClick = { onDelete(eq) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, null, tint = CatppuccinMocha.Red, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/mykaradainam/ui/settings/
git commit -m "feat: add Settings screen with equipment management and electricity rate display"
```

---

### Task 16: Integration & Final Polish

**Files:**
- Modify: `app/src/main/java/com/mykaradainam/navigation/NavGraph.kt` (wire Confirm parse result)
- Create: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: Create Vietnamese strings.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">MyKaraDainam</string>
    <string name="room_1">Phòng 1</string>
    <string name="room_2">Phòng 2</string>
    <string name="start">Bắt đầu</string>
    <string name="finish">Kết thúc</string>
    <string name="invoice">Hóa đơn</string>
    <string name="reports">Báo cáo</string>
    <string name="settings">Cài đặt</string>
    <string name="save">Lưu</string>
    <string name="cancel">Hủy</string>
    <string name="retry">Thử lại</string>
    <string name="no_connection">Không có kết nối</string>
    <string name="processing">Đang xử lý...</string>
</resources>
```

- [ ] **Step 2: Verify full build compiles**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Run all unit tests**

Run: `./gradlew test`
Expected: ALL PASS

- [ ] **Step 4: Final commit**

```bash
git add .
git commit -m "feat: complete MyKaraDainam v1.0 — karaoke room management with AI-powered invoice capture"
```

---

## Execution Notes

**Build order matters:** Tasks 1-7 (foundation) must run sequentially. Tasks 8-15 (UI screens) can run in parallel after Task 7 completes, BUT Task 9 (Navigation) should come before Tasks 10-15 since screens need route references.

**Shared state for Confirm screen:** `SharedInvoiceDataHolder` singleton bridges Camera/Voice → Confirm. Camera/Voice ViewModels call `sharedInvoiceData.set(result)`, ConfirmViewModel reads via `sharedInvoiceData.get()` in `init` and clears it.

**Camera/Audio permissions:** Runtime permission handling is built into CameraScreen and VoiceScreen using `rememberLauncherForActivityResult`. Both screens show a permission request UI if not granted.

**Proguard:** Create an empty `app/proguard-rules.pro` file in Task 1 to prevent release build failure.

**Testing on device:** OCR and voice features require a physical device with camera and microphone. Use Android emulator for UI testing, physical device for AI integration testing.
