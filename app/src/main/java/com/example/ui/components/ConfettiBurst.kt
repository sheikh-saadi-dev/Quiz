package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import kotlin.random.Random

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val vr: Float
)

@Composable
fun ConfettiBurst(
    modifier: Modifier = Modifier,
    particleCount: Int = 120
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFF6C5CE7),
            Color(0xFF00D2D3),
            Color(0xFF00B894),
            Color(0xFFFFD15C),
            Color(0xFFFF7675),
            Color(0xFFFD79A8)
        )
        List(particleCount) {
            ConfettiParticle(
                x = 0.5f,
                y = 0.4f,
                vx = (Random.nextFloat() - 0.5f) * 1.8f,
                vy = -Random.nextFloat() * 1.5f - 0.3f,
                size = Random.nextFloat() * 14f + 8f,
                color = colors.random(),
                rotation = Random.nextFloat() * 360f,
                vr = (Random.nextFloat() - 0.5f) * 720f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .testTag("confetti_canvas")
    ) {
        val p = progress.value
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { particle ->
            val px = (particle.x + particle.vx * p) * canvasWidth
            // Gravity acceleration effect
            val py = (particle.y + particle.vy * p + 0.8f * p * p) * canvasHeight
            val rotation = particle.rotation + particle.vr * p

            drawRect(
                color = particle.color.copy(alpha = (1f - p * 0.8f).coerceIn(0f, 1f)),
                topLeft = Offset(px, py),
                size = Size(particle.size, particle.size * 0.6f)
            )
        }
    }
}
