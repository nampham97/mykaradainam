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
