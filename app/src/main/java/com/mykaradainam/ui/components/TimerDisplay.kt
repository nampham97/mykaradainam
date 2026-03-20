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
