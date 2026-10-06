package com.example.ui.components

import com.example.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TourHotspot
import com.example.model.TourScene
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun Tour360Viewer(
    scenes: List<TourScene>,
    modifier: Modifier = Modifier,
    initialSceneIndex: Int = 0,
    showControls: Boolean = true,
    onCloseClick: (() -> Unit)? = null
) {
    if (scenes.isEmpty()) return

    var currentSceneIndex by remember { mutableIntStateOf(initialSceneIndex.coerceIn(0, scenes.size - 1)) }
    val currentScene = scenes[currentSceneIndex]

    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.2f) }
    var autoRotate by remember { mutableStateOf(false) }
    var vrModeDialog by remember { mutableStateOf(false) }
    var activeHotspotInfo by remember { mutableStateOf<TourHotspot?>(null) }

    // Pulsating animation for hotspots
    val infiniteTransition = rememberInfiniteTransition(label = "hotspot_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Auto rotate feature
    LaunchedEffect(autoRotate) {
        while (autoRotate) {
            panX -= 1.5f
            delay(16)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .background(Color.Black)
            .clip(RoundedCornerShape(16.dp))
    ) {
        val containerWidth = maxWidth.value
        val containerHeight = maxHeight.value

        // Panorama viewport with drag and zoom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(1.0f, 3.0f)
                        panX += pan.x
                        panY = (panY + pan.y).coerceIn(-200f, 200f)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panX += dragAmount.x
                        panY = (panY + dragAmount.y).coerceIn(-200f, 200f)
                    }
                }
        ) {
            // High-resolution panoramic room image (from local stitched file or drawable resource)
            if (!currentScene.imagePath.isNullOrEmpty()) {
                val localBitmap = remember(currentScene.imagePath) {
                    try {
                        android.graphics.BitmapFactory.decodeFile(currentScene.imagePath)?.asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
                if (localBitmap != null) {
                    Image(
                        bitmap = localBitmap,
                        contentDescription = currentScene.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = zoomScale
                                scaleY = zoomScale
                                translationX = panX % (size.width.takeIf { it > 0 } ?: 1000f)
                                translationY = panY
                            }
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = if (currentScene.drawableResId != 0) currentScene.drawableResId else R.drawable.img_tour_sample),
                    contentDescription = currentScene.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = zoomScale
                            scaleY = zoomScale
                            translationX = panX % (size.width.takeIf { it > 0 } ?: 1000f)
                            translationY = panY
                        }
                )
            }

            // Render interactive hotspots
            currentScene.hotspots.forEach { hotspot ->
                // Calculate hotspot position with panning and zoom
                val normX = (hotspot.xPercent * containerWidth * zoomScale + panX) % containerWidth
                val normY = (hotspot.yPercent * containerHeight * zoomScale + panY).coerceIn(40f, containerHeight - 60f)

                Box(
                    modifier = Modifier
                        .offset { IntOffset(normX.roundToInt(), normY.roundToInt()) }
                        .scale(pulseScale)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AccentOrange)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable {
                            if (hotspot.targetSceneId != null) {
                                val targetIdx = scenes.indexOfFirst { it.id == hotspot.targetSceneId }
                                if (targetIdx >= 0) {
                                    currentSceneIndex = targetIdx
                                    panX = 0f
                                    panY = 0f
                                }
                            } else {
                                activeHotspotInfo = hotspot
                            }
                        }
                        .testTag("hotspot_${hotspot.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hotspot.targetSceneId != null) Icons.Default.NearMe else Icons.Default.Info,
                        contentDescription = hotspot.title,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Top Gradient overlay with scene title and close/VR button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
                .padding(12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // VR Mode & 360 Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = BrandSecondary.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RotateRight,
                                contentDescription = "360",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "تور ۳۶۰° فعال",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Room Title
                    Text(
                        text = currentScene.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Action buttons (VR, Close)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { vrModeDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = "حالت واقعیت مجازی VR",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (onCloseClick != null) {
                        IconButton(
                            onClick = onCloseClick,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Control Panel on right side (Zoom in/out, Auto Rotate, Compass)
        if (showControls) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Compass / Gyro Indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { panX = 0f; panY = 0f },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "قطب نما",
                        tint = BrandGold,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate((-panX / 3f) % 360)
                    )
                }

                // Auto rotate toggle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (autoRotate) BrandPrimary else Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { autoRotate = !autoRotate },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = "چرخش خودکار",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Zoom In
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { zoomScale = (zoomScale + 0.25f).coerceAtMost(3.0f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "بزرگ‌نمایی",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Zoom Out
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        .clickable { zoomScale = (zoomScale - 0.25f).coerceAtLeast(1.0f) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "کوچک‌نمایی",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Bottom Room Switcher Carousel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = "فضاهای ملک (برای گردش بین اتاق‌ها لمس کنید):",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(scenes.mapIndexed { idx, sc -> Pair(idx, sc) }) { (idx, scene) ->
                        val isSelected = idx == currentSceneIndex
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    currentSceneIndex = idx
                                    panX = 0f
                                    panY = 0f
                                }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) BrandGold else Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            color = if (isSelected) BrandPrimary.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MeetingRoom,
                                    contentDescription = null,
                                    tint = if (isSelected) BrandGold else Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = scene.name,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Hotspot Info Modal
    activeHotspotInfo?.let { hotspot ->
        AlertDialog(
            onDismissRequest = { activeHotspotInfo = null },
            title = {
                Text(
                    text = hotspot.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary
                )
            },
            text = {
                Text(
                    text = hotspot.infoText ?: "اطلاعات تکمیلی این بخش ثبت شده است.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { activeHotspotInfo = null }) {
                    Text("متوجه شدم", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // VR Mode Dialog
    if (vrModeDialog) {
        AlertDialog(
            onDismissRequest = { vrModeDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.ViewInAr, contentDescription = null, tint = BrandPrimary)
                    Text("حالت واقعیت مجازی (VR)", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "برای تجربه واقعیت مجازی و قدم زدن در ملک با عینک‌های VR (مانند Google Cardboard یا Oculus)، گوشی را به صورت افقی قرار دهید.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "قابلیت حسگر ژیروسکوپ گوشی فعال است و همگام با چرخش سر شما دید ملک تغییر می‌کند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        autoRotate = true
                        vrModeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("فعال‌سازی سنسور ۳۶۰")
                }
            },
            dismissButton = {
                TextButton(onClick = { vrModeDialog = false }) {
                    Text("بستن")
                }
            }
        )
    }
}
