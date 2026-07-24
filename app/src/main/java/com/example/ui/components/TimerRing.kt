package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.WrongRed

@Composable
fun TimerRing(
    secondsRemaining: Int,
    totalSeconds: Int = 15,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp
) {
    val fraction = (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    val targetColor = when {
        fraction > 0.5f -> CorrectGreen
        fraction > 0.25f -> Color(0xFFF1C40F) // Yellow
        else -> WrongRed
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(300),
        label = "timer_color"
    )

    Box(
        modifier = Modifier
            .size(size)
            .testTag("timer_ring"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            // Background ring track
            drawCircle(
                color = animatedColor.copy(alpha = 0.2f),
                style = Stroke(width = strokeWidth.toPx())
            )

            // Animated foreground sweep arc
            drawArc(
                color = animatedColor,
                startAngle = -90f,
                sweepAngle = 360f * fraction,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$secondsRemaining",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = animatedColor
        )
    }
}
