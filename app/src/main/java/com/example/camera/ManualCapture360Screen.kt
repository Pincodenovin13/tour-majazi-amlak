@file:OptIn(androidx.camera.camera2.interop.ExperimentalCamera2Interop::class)
@file:SuppressLint("UnsafeOptInUsageError")

package com.example.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.R
import com.example.metadata.GPanoMetadata
import com.example.model.RoomCapturePreset
import com.example.sensor.currentDisplayRotation
import com.example.storage.SphereImageStore
import com.example.storage.SphereImageStore.StitchedSphere
import com.example.stitching.CameraPose
import com.example.stitching.PhotoSphereStitcher
import com.example.stitching.SphereFrame
import com.example.stitching.StitchProgress
import com.example.stitching.StitchStage
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.SphereAccent
import com.example.ui.util.PersianUtils
import com.example.util.WatermarkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "ManualCapture360"

/**
 * ManualCapture360Screen:
 * Works on ALL Android devices from Android 7 (API 24) and up without requiring a gyroscope.
 * Supports:
 * - Room-by-room capturing plan
 * - Visual circular guide with 8 dots and rotation arrow
 * - Approximate 45° step poses for OpenCV feature matching & PoseRefinement
 * - Automatic branding watermark with agency name and phone number
 * - Fallback card for camera errors with retry button
 */
@Composable
fun ManualCapture360Screen(
    rooms: List<RoomCapturePreset> = listOf(RoomCapturePreset("living_room", "پذیرایی", "Living Room", 8)),
    agencyName: String = "املاک مدرن شمیران",
    agentPhone: String = "۰۹۱۲۳۴۵۶۷۸۹",
    onRoomStitched: (room: RoomCapturePreset, sphere: StitchedSphere) -> Unit,
    onAllRoomsComplete: () -> Unit,
    onBackClick: () -> Unit,
    onSelectRoomsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val feedback = rememberCaptureFeedback()

    // Keep screen active
    val rootView = LocalView.current
    DisposableEffect(rootView) {
        rootView.keepScreenOn = true
        onDispose {
            rootView.keepScreenOn = false
            feedback.release()
        }
    }

    // Room tour sequence state
    var currentRoomIndex by remember { mutableIntStateOf(0) }
    val currentRoom = rooms.getOrElse(currentRoomIndex) { rooms.first() }
    val targetSteps = currentRoom.photoCount

    // Photos captured for current room
    val capturedPhotos = remember { mutableStateListOf<File>() }
    var isCapturing by remember { mutableStateOf(false) }

    // Instruction dialog state (shown before first shot of each room)
    var showInstructionDialog by remember { mutableStateOf(true) }

    // Stitching state
    var isStitching by remember { mutableStateOf(false) }
    var stitchStatusText by remember { mutableStateOf("در حال دوخت عکس‌ها...") }

    // Gallery review dialog
    var showGalleryReview by remember { mutableStateOf(false) }
    var galleryPreviewBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Camera bind state
    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var bindAttempt by remember { mutableIntStateOf(0) }

    val deviceProfile = remember { SphereDeviceProfile.forDevice() }

    // Handle back button
    BackHandler {
        if (capturedPhotos.isNotEmpty()) {
            // Confirm cancel
            onBackClick()
        } else {
            onBackClick()
        }
    }

    // CameraX binding effect
    LaunchedEffect(lifecycleOwner, bindAttempt) {
        cameraError = null
        val cameraProvider = try {
            suspendCoroutine<ProcessCameraProvider> { continuation ->
                ProcessCameraProvider.getInstance(context).addListener(
                    {
                        try {
                            continuation.resume(ProcessCameraProvider.getInstance(context).get())
                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    },
                    ContextCompat.getMainExecutor(context),
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "ProcessCameraProvider failed", e)
            cameraError = "دوربین در دسترس نیست. لطفاً اپ را ببندید و دوباره باز کنید."
            return@LaunchedEffect
        }

        val cameraSelector = widestCameraSelector(context) ?: CameraSelector.DEFAULT_BACK_CAMERA

        val preview = Preview.Builder()
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                    .build()
            )
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

        val capture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(
                                deviceProfile.captureMaxLongEdgePx,
                                deviceProfile.captureMaxLongEdgePx * 3 / 4,
                            ),
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
                        )
                    )
                    .build()
            )
            .setTargetRotation(context.currentDisplayRotation())
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, capture)
            imageCapture = capture
        } catch (e: Exception) {
            Log.w(TAG, "Widest camera failed, binding default", e)
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )
                imageCapture = capture
            } catch (e2: Exception) {
                Log.e(TAG, "Camera binding completely failed", e2)
                cameraError = "دوربین در دسترس نیست. لطفاً اپ را ببندید و دوباره باز کنید."
            }
        }
    }

    // Function to take a manual photo
    fun takeManualPhoto() {
        val capture = imageCapture ?: return
        if (isCapturing || capturedPhotos.size >= targetSteps) return

        isCapturing = true
        val index = capturedPhotos.size
        val outputDir = File(context.cacheDir, "manual_sphere_${System.currentTimeMillis()}")
        if (!outputDir.exists()) outputDir.mkdirs()
        val photoFile = File(outputDir, "manual_frame_%02d.jpg".format(index))
        val options = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        capture.takePicture(
            options,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    isCapturing = false
                    capturedPhotos.add(photoFile)
                    // Vibration and shutter sound
                    feedback.onFrameCaptured()
                }

                override fun onError(exception: ImageCaptureException) {
                    isCapturing = false
                    Log.e(TAG, "Manual photo capture error", exception)
                    Toast.makeText(context, "خطا در ثبت عکس: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Function to stitch captured manual frames using approximate poses + ORB PoseRefinement
    fun startManualStitch() {
        if (capturedPhotos.size < 2 || isStitching) return
        isStitching = true
        stitchStatusText = "در حال آماده‌سازی و بهینه‌سازی زوایای عکس‌ها..."

        scope.launch(Dispatchers.Default) {
            val stepDegrees = 360f / targetSteps
            val frames = capturedPhotos.mapIndexed { index, file ->
                // Approximate pose: yaw rotates by stepDegrees (e.g. 0, 45, 90, 135, ...)
                val yaw = (index * stepDegrees) % 360f
                val pose = CameraPose(
                    yawDegrees = yaw,
                    pitchDegrees = 0f,
                    rollDegrees = 0f,
                    matrix = null
                )
                SphereFrame(file = file, pose = pose)
            }

            stitchStatusText = "در حال دوخت عکس‌های ۳۶۰ درجه با هوش تصویری OpenCV..."

            // Use PhotoSphereStitcher with useRefinement = true to correct misalignments
            PhotoSphereStitcher.stitchPhotos(
                frames = frames,
                horizontalFovDegrees = 70f,
                verticalFovDegrees = 55f,
                maxInputDimension = deviceProfile.stitchMaxInputDimension,
                maxOutputWidth = deviceProfile.stitchMaxOutputWidth,
                portraitRotationDegrees = 90,
                useRefinement = true, // PoseRefiner ORB feature matching enabled
                useSeams = true,
                longitudeSpanDegrees = 360f,
                latitudeSpanDegrees = 180f,
                onProgress = { progress ->
                    stitchStatusText = when (progress.stage) {
                        StitchStage.Preparing -> "آماده‌سازی عکس‌ها..."
                        StitchStage.Reading -> "خواندن فریم‌های ورودی..."
                        StitchStage.Refining -> "اصلاح خودکار زاویه چرخش و انطباق..."
                        StitchStage.Seaming -> "محاسبه مرزهای همپوشانی فریم‌ها..."
                        StitchStage.Stitching -> "ادغام و تلفیق درزهای تصویر ۳۶۰°..."
                        StitchStage.Projecting -> "تولید تصویر پانورامای نهایی..."
                    }
                }
            ).onSuccess { sphereBitmap ->
                stitchStatusText = "در حال اعمال واترمارک رسمی مشاور..."
                // Step 6: Add watermark to the final stitched panorama
                val watermarkedBitmap = WatermarkUtils.applyAgentWatermark(
                    srcBitmap = sphereBitmap,
                    agencyName = agencyName,
                    agentPhone = agentPhone
                )

                stitchStatusText = "در حال ذخیره‌سازی پانورامای ۳۶۰ درجه..."
                val stitchedSphere = try {
                    withContext(Dispatchers.IO) {
                        SphereImageStore.writeStitchedSphere(
                            context = context,
                            bitmap = watermarkedBitmap,
                            gpano = GPanoMetadata.forFullPano(
                                watermarkedBitmap.width,
                                watermarkedBitmap.height
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed writing stitched sphere", e)
                    null
                } finally {
                    sphereBitmap.recycle()
                    if (watermarkedBitmap != sphereBitmap) {
                        watermarkedBitmap.recycle()
                    }
                }

                withContext(Dispatchers.Main) {
                    isStitching = false
                    if (stitchedSphere != null) {
                        onRoomStitched(currentRoom, stitchedSphere)

                        // Clean up temporary manual frames
                        capturedPhotos.forEach { it.delete() }
                        capturedPhotos.clear()

                        // Check next room or finish
                        if (currentRoomIndex + 1 < rooms.size) {
                            currentRoomIndex++
                            showInstructionDialog = true
                        } else {
                            onAllRoomsComplete()
                        }
                    } else {
                        Toast.makeText(context, "خطا در ذخیره پانوراما", Toast.LENGTH_LONG).show()
                    }
                }
            }.onFailure { error ->
                Log.e(TAG, "Stitching error", error)
                withContext(Dispatchers.Main) {
                    isStitching = false
                    Toast.makeText(context, "خطا در دوخت عکس‌ها: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Full screen layout with dark theme, yellow/orange accents
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ==========================================
            // TOP SECTION: Live CameraX preview (60% height)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.60f)
                    .background(Color.Black)
            ) {
                // Live Viewfinder
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )

                // MIDDLE SECTION: Circular guide overlay on preview
                ManualCaptureOverlay(
                    totalSteps = targetSteps,
                    capturedCount = capturedPhotos.size,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Bar Chrome (Room label & Close button)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Room indicator badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellow.copy(alpha = 0.5f)),
                        modifier = Modifier.then(
                            if (onSelectRoomsClick != null) Modifier.clickable { onSelectRoomsClick() } else Modifier
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = AccentYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "اتاق ${PersianUtils.toPersianDigits(currentRoomIndex + 1)} از ${PersianUtils.toPersianDigits(rooms.size)}: ${currentRoom.persianName}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (onSelectRoomsClick != null) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "انتخاب اتاق",
                                    tint = AccentYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Help / Instructions Button
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            IconButton(onClick = { showInstructionDialog = true }) {
                                Icon(Icons.Default.HelpOutline, contentDescription = "راهنما", tint = AccentYellow)
                            }
                        }

                        // Close button (X)
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Default.Close, contentDescription = "بستن", tint = Color.White)
                            }
                        }
                    }
                }

                // Middle Counter & Guidance text below circular guide
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.70f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = "${PersianUtils.toPersianDigits(capturedPhotos.size)} از ${PersianUtils.toPersianDigits(targetSteps)}",
                            color = AccentYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "گوشی را به آرامی بچرخانید و روی هر نقطه یک عکس بگیرید",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ==========================================
            // BOTTOM SECTION: Controls & Shutter (40% height)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.40f)
                    .background(DarkSurface)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Agency Watermark Preview Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "واترمارک: $agencyName - $agentPhone",
                                color = Color.White.copy(alpha = 0.65f),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SphereAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Main Shutter / Stitch Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Cancel / Reset Button (bottom-left in RTL)
                        OutlinedButton(
                            onClick = {
                                if (capturedPhotos.isNotEmpty()) {
                                    capturedPhotos.forEach { it.delete() }
                                    capturedPhotos.clear()
                                } else {
                                    onBackClick()
                                }
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White.copy(alpha = 0.8f)
                            ),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "تنظیم مجدد", tint = Color.White)
                        }

                        // Large Action Button: "عکس بگیر" (80dp) or "دوخت عکس‌ها"
                        val isReadyToStitch = capturedPhotos.size >= targetSteps
                        if (isReadyToStitch) {
                            Button(
                                onClick = ::startManualStitch,
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentOrange,
                                    contentColor = Color.White
                                ),
                                enabled = !isStitching,
                                modifier = Modifier
                                    .height(72.dp)
                                    .padding(horizontal = 8.dp)
                                    .testTag("stitch_manual_photos_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                                    Text(
                                        text = "دوخت عکس‌ها",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            }
                        } else {
                            // Circular 80dp Shutter Button
                            Surface(
                                shape = CircleShape,
                                color = AccentYellow,
                                shadowElevation = 10.dp,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clickable(enabled = !isCapturing && imageCapture != null) {
                                        takeManualPhoto()
                                    }
                                    .testTag("manual_capture_shutter_btn")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isCapturing) {
                                        CircularProgressIndicator(
                                            color = Color.Black,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    } else {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.CameraAlt,
                                                contentDescription = "عکس بگیر",
                                                tint = Color.Black,
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Text(
                                                text = "عکس بگیر",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Gallery Icon to review captured photos (bottom-right in RTL)
                        Surface(
                            shape = CircleShape,
                            color = DarkSurfaceCard,
                            modifier = Modifier
                                .size(54.dp)
                                .clickable {
                                    if (capturedPhotos.isNotEmpty()) {
                                        showGalleryReview = true
                                    } else {
                                        Toast.makeText(context, "هنوز عکسی ثبت نشده است", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.PhotoLibrary,
                                    contentDescription = "گالری عکس‌ها",
                                    tint = if (capturedPhotos.isNotEmpty()) AccentYellow else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    // Progress info text
                    Text(
                        text = if (capturedPhotos.size >= targetSteps) {
                            "همه عکس‌های اتاق ثبت شد! دکمه 'دوخت عکس‌ها' را لمس کنید."
                        } else {
                            "برای زاویه بعد (~۴۵ درجه) بچرخید و عکس بگیرید."
                        },
                        color = if (capturedPhotos.size >= targetSteps) SphereAccent else Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Camera Error Fallback Card
        cameraError?.let { msg ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f)),
                color = Color.Transparent
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                            Text(
                                text = msg,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { bindAttempt++ },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("تلاش مجدد", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Stitching Progress Dialog
        if (isStitching) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.85f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(24.dp)) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            CircularProgressIndicator(
                                color = AccentYellow,
                                modifier = Modifier.size(54.dp)
                            )
                            Text(
                                text = "در حال دوخت عکس‌ها...",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stitchStatusText,
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Instruction Screen Dialog
        if (showInstructionDialog) {
            ManualCaptureInstructionDialog(
                roomName = currentRoom.persianName,
                photoCount = targetSteps,
                onDismiss = { showInstructionDialog = false }
            )
        }

        // Gallery Review Dialog
        if (showGalleryReview) {
            AlertDialog(
                onDismissRequest = {
                    showGalleryReview = false
                    galleryPreviewBitmap?.recycle()
                    galleryPreviewBitmap = null
                },
                containerColor = DarkSurface,
                title = {
                    Text(
                        text = "عکس‌های ثبت شده (${PersianUtils.toPersianDigits(capturedPhotos.size)} از ${PersianUtils.toPersianDigits(targetSteps)})",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "پیش‌نمایش بندانگشتی فریم‌های گرفته شده:",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(capturedPhotos) { idx, file ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                                    modifier = Modifier.size(70.dp, 90.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Text(
                                            text = "عکس ${PersianUtils.toPersianDigits(idx + 1)}",
                                            color = AccentYellow,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showGalleryReview = false },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentYellow, contentColor = Color.Black)
                    ) {
                        Text("ادامه عکاسی", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
