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
