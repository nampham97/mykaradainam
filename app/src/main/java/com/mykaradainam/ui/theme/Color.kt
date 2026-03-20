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
