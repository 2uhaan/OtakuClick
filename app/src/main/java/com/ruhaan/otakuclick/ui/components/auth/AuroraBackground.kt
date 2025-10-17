package com.ruhaan.otakuclick.ui.components.auth

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Aurora animated background component
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier
) {
    // Animation states
    val infiniteTransition = rememberInfiniteTransition(label = "aurora")

    // Multiple animation values for different effects
    val wave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val wave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "colorShift"
    )

    // Aurora colors
    val auroraColors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF8B5CF6), // Purple
        Color(0xFF06B6D4), // Cyan
        Color(0xFFEC4899), // Pink
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B)  // Amber
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)) // Dark base
    ) {
        // Multiple gradient layers for depth
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(60.dp) // Heavy blur for aurora effect
        ) {
            drawAuroraLayer(
                wave1 = wave1,
                wave2 = wave2,
                colorShift = colorShift,
                colors = auroraColors,
                layer = 0
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(40.dp) // Medium blur
        ) {
            drawAuroraLayer(
                wave1 = wave1 + 0.5f,
                wave2 = wave2 + 0.3f,
                colorShift = colorShift,
                colors = auroraColors,
                layer = 1
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp) // Light blur
        ) {
            drawAuroraLayer(
                wave1 = wave1 + 1f,
                wave2 = wave2 + 0.7f,
                colorShift = colorShift,
                colors = auroraColors,
                layer = 2
            )
        }

        // Subtle particle overlay
        ParticleOverlay(
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Draw individual aurora layer
private fun DrawScope.drawAuroraLayer(
    wave1: Float,
    wave2: Float,
    colorShift: Float,
    colors: List<Color>,
    layer: Int
) {
    val width = size.width
    val height = size.height

    // Create gradient brush
    val gradientColors = colors.map { color ->
        color.copy(alpha = 0.3f + (colorShift * 0.2f))
    }

    val brush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(
            x = width * 0.2f + sin(wave1 + layer) * width * 0.3f,
            y = height * 0.1f
        ),
        end = Offset(
            x = width * 0.8f + cos(wave2 + layer) * width * 0.3f,
            y = height * 0.9f
        )
    )

    // Draw flowing shape
    val path = Path().apply {
        moveTo(0f, height * 0.3f)

        // Create wavy path
        for (x in 0..width.toInt() step 20) {
            val progress = x / width
            val y = height * 0.5f +
                    sin(wave1 + progress * 4 + layer) * height * 0.2f +
                    cos(wave2 + progress * 2 + layer) * height * 0.1f

            lineTo(x.toFloat(), y)
        }

        lineTo(width, height)
        lineTo(0f, height)
        close()
    }

    drawPath(
        path = path,
        brush = brush,
        alpha = 0.6f
    )
}

// Floating particle overlay
@Composable
private fun ParticleOverlay(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    val particleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleOffset"
    )

    Canvas(modifier = modifier) {
        // Draw floating particles
        repeat(15) { i ->
            val x = (size.width * 0.1f + (i * size.width * 0.8f / 15f) +
                    sin(particleOffset * 2 * PI + i) * 50f).coerceIn(0.0, size.width.toDouble()).toFloat()
            val y = (size.height * 0.2f + (particleOffset + i * 0.1f) % 1f * size.height * 0.6f)

            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = 2f + sin(particleOffset * 3 + i) * 1f,
                center = Offset(x, y)
            )
        }
    }
}