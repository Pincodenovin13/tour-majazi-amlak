package com.example.camera

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * ManualCaptureOverlay:
 * Circular guide overlay with 200dp diameter in the center of the preview.
 * - Circular track guide with translucent border
 * - Rotation arrow in the center pointing clockwise to the next target position
 * - N dots (default 8) arranged in a circle:
 *    * Green filled = already captured
 *    * Yellow pulsing = current position
 *    * Hollow grey/white = not captured yet
 */
@Composable
fun ManualCaptureOverlay(
    totalSteps: Int,
    capturedCount: Int,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = 100.dp.toPx()

            // 1. Draw outer circular guide ring
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Inner subtle background fill
            drawCircle(
                color = Color.Black.copy(alpha = 0.20f),
                radius = radius,
                center = center
            )

            // 2. Draw Rotation Arrow in the center pointing clockwise (right)
            val arrowRadius = 40.dp.toPx()
            val arrowPath = Path().apply {
                // Arc segment indicating turn right / clockwise
                addArc(
                    oval = androidx.compose.ui.geometry.Rect(
                        center.x - arrowRadius,
                        center.y - arrowRadius,
                        center.x + arrowRadius,
                        center.y + arrowRadius
                    ),
                    startAngleDegrees = -80f,
                    sweepAngleDegrees = 140f
                )
            }
            drawPath(
                path = arrowPath,
                color = Color(0xFFFFC107).copy(alpha = 0.85f),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Arrow head at end of arc (~ 60 degrees)
            val arrowAngleRad = Math.toRadians(60.0)
            val arrowHeadX = center.x + arrowRadius * cos(arrowAngleRad).toFloat()
            val arrowHeadY = center.y + arrowRadius * sin(arrowAngleRad).toFloat()

            val headPath = Path().apply {
                moveTo(arrowHeadX, arrowHeadY)
                lineTo(arrowHeadX - 16f, arrowHeadY - 4f)
                moveTo(arrowHeadX, arrowHeadY)
                lineTo(arrowHeadX - 4f, arrowHeadY - 16f)
            }
            drawPath(
                path = headPath,
                color = Color(0xFFFFC107),
                style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // 3. Draw dots arranged around the circle
            val stepAngle = (2 * PI / totalSteps).toFloat()
            // Start at top (-PI/2)
            val startAngle = -PI.toFloat() / 2f

            for (i in 0 until totalSteps) {
                val angle = startAngle + i * stepAngle
                val dotX = center.x + radius * cos(angle)
                val dotY = center.y + radius * sin(angle)
                val dotCenter = Offset(dotX, dotY)

                when {
                    i < capturedCount -> {
                        // Green filled = already captured
                        drawCircle(
                            color = Color(0xFF4CAF50),
                            radius = 7.dp.toPx(),
                            center = dotCenter
                        )
                        // White check/inner glow
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = dotCenter
                        )
                    }
                    i == capturedCount -> {
                        // Yellow pulsing dot = current position
                        // Outer pulse ring
                        drawCircle(
                            color = Color(0xFFFFC107).copy(alpha = 0.40f),
                            radius = 11.dp.toPx() * pulseScale,
                            center = dotCenter
                        )
                        // Center yellow solid
                        drawCircle(
                            color = Color(0xFFFFC107),
                            radius = 8.dp.toPx(),
                            center = dotCenter
                        )
                    }
                    else -> {
                        // Hollow dot = not captured yet
                        drawCircle(
                            color = Color.White.copy(alpha = 0.6f),
                            radius = 6.dp.toPx(),
                            center = dotCenter,
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.4f),
                            radius = 5.dp.toPx(),
                            center = dotCenter
                        )
                    }
                }
            }
        }
    }
}
