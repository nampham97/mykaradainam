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
