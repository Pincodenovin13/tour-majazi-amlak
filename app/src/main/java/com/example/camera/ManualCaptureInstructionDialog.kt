package com.example.camera

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.util.PersianUtils

/**
 * Instruction dialog/screen shown before starting capture for each room:
 * Diagram of person standing in the center of the room, holding phone horizontally, with rotation arrow.
 * Six Persian guidance steps.
 */
@Composable
fun ManualCaptureInstructionDialog(
    roomName: String,
    photoCount: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.88f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AccentYellow.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = AccentYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "راهنمای عکاسی دستی",
                                    color = AccentYellow,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                        }
                    }

                    Text(
                        text = "برای عکسبرداری از «$roomName»:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    // Illustration Diagram: Person in room with rotation arrow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceCard),
                        contentAlignment = Alignment.Center
                    ) {
                        RoomStanceIllustration()
                    }

                    // 6 Detailed Steps
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InstructionStepRow(number = "۱", text = "در مرکز اتاق بایستید")
                        InstructionStepRow(number = "۲", text = "گوشی را به صورت افقی (Landscape) یا عمودی پایدار بگیرید")
                        InstructionStepRow(number = "۳", text = "گوشی را در ارتفاع سینه ثابت نگه دارید")
                        InstructionStepRow(number = "۴", text = "به آرامی به دور خود به راست بچرخید")
                        InstructionStepRow(number = "۵", text = "در هر ۴۵ درجه، دکمه 'عکس بگیر' را بزنید")
                        InstructionStepRow(
                            number = "۶",
                            text = "پس از ${PersianUtils.toPersianDigits(photoCount)} عکس، دکمه 'دوخت عکس‌ها' را بزنید"
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Start Capture Button
                    Button(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentYellow,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "شروع عکاسی از $roomName",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionStepRow(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = AccentYellow.copy(alpha = 0.2f),
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = AccentYellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Vector illustration of a room, person in center with phone, and rotation circular arrows.
 */
@Composable
private fun RoomStanceIllustration() {
    Canvas(modifier = Modifier.size(220.dp, 120.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)

        // 1. Room boundary outline (isometric rectangle)
        val roomPath = Path().apply {
            moveTo(20f, 15f)
            lineTo(size.width - 20f, 15f)
            lineTo(size.width - 20f, size.height - 15f)
            lineTo(20f, size.height - 15f)
            close()
        }
        drawPath(
            path = roomPath,
            color = Color.White.copy(alpha = 0.15f),
            style = Stroke(width = 2.dp.toPx())
        )

        // 2. Circular rotation ring around person
        val radius = 42.dp.toPx()
        drawCircle(
            color = Color(0xFFFFC107).copy(alpha = 0.35f),
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // 3. Rotation Arrow
        val arrowAngleRad = Math.toRadians(45.0)
        val arrowX = center.x + radius * kotlin.math.cos(arrowAngleRad).toFloat()
        val arrowY = center.y + radius * kotlin.math.sin(arrowAngleRad).toFloat()

        val headPath = Path().apply {
            moveTo(arrowX, arrowY)
            lineTo(arrowX - 14f, arrowY + 2f)
            moveTo(arrowX, arrowY)
            lineTo(arrowX + 2f, arrowY - 14f)
        }
        drawPath(
            path = headPath,
            color = Color(0xFFFFC107),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // 4. Person in center (Head + Torso + Phone)
        // Head
        drawCircle(
            color = Color.White,
            radius = 10.dp.toPx(),
            center = Offset(center.x, center.y - 14.dp.toPx())
        )
        // Torso / Shoulders
        drawLine(
            color = Color.White,
            start = Offset(center.x - 14.dp.toPx(), center.y + 2.dp.toPx()),
            end = Offset(center.x + 14.dp.toPx(), center.y + 2.dp.toPx()),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Phone held out in hands (small yellow rectangle)
        drawRect(
            color = Color(0xFFFFC107),
            topLeft = Offset(center.x - 8.dp.toPx(), center.y + 10.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(16.dp.toPx(), 8.dp.toPx())
        )
    }
}
