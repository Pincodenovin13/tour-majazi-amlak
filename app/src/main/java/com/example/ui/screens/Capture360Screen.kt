package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.camera.ManualCapture360Screen
import com.example.camera.PhotoSphereCameraScreen
import com.example.camera.RoomSelectionScreen
import com.example.data.AppRepository
import com.example.model.CaptureMode
import com.example.model.RoomCapturePreset
import com.example.model.TourScene
import com.example.result.PanoramaResultScreen
import com.example.sensor.rememberOrientationTracker
import com.example.storage.SphereImageStore.StitchedSphere

/**
 * Capture360Screen:
 * Real CameraX + OpenCV photo sphere capture screen supporting both:
 * 1. AUTO_GYRO: Automatic guided capture when a gyroscope is available.
 * 2. MANUAL: Manual capture mode working on ALL Android devices (API 24+) without gyroscope.
 * Automatically switches to MANUAL if no gyroscope is present, or allows toggling.
 */
@Composable
fun Capture360Screen(
    repository: AppRepository,
    onCaptureFinished: (TourScene) -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tracker = rememberOrientationTracker()
    val isGyroAvailable = tracker.isSensorAvailable

    // Mode state: Default to AUTO_GYRO if sensor available, else MANUAL
    var captureMode by remember {
        mutableStateOf(if (isGyroAvailable) CaptureMode.AUTO_GYRO else CaptureMode.MANUAL)
    }

    // Inform user if device lacks gyroscope
    var hasShownNoGyroToast by remember { mutableStateOf(false) }
    if (!isGyroAvailable && !hasShownNoGyroToast) {
        hasShownNoGyroToast = true
        Toast.makeText(
            context,
            "دستگاه شما ژیروسکوپ ندارد. از حالت دستی استفاده کنید.",
            Toast.LENGTH_LONG
        ).show()
    }

    // Step state for manual flow
    var isSelectingRooms by remember { mutableStateOf(!isGyroAvailable) }
    var selectedRooms by remember {
        mutableStateOf(listOf(RoomCapturePreset("living_room", "پذیرایی", "Living Room", 8)))
    }

    // Active result panorama
    var activeSphere by remember { mutableStateOf<StitchedSphere?>(null) }
    var activeRoomForSphere by remember { mutableStateOf<RoomCapturePreset?>(null) }

    val agencyName by repository.agencyName.collectAsState()
    val userPhone by repository.userPhone.collectAsState()

    val sphere = activeSphere
    if (sphere != null) {
        PanoramaResultScreen(
            sphere = sphere,
            onTakeAnother = {
                activeSphere = null
            },
            onSaveToTour = { scene ->
                val namedScene = if (activeRoomForSphere != null) {
                    scene.copy(name = activeRoomForSphere?.persianName ?: scene.name)
                } else {
                    scene
                }
                repository.addStitchedScene(namedScene)
                onCaptureFinished(namedScene)
            },
            onBackClick = {
                activeSphere = null
            },
            modifier = modifier
        )
    } else if (captureMode == CaptureMode.MANUAL) {
        if (isSelectingRooms) {
            RoomSelectionScreen(
                onRoomsSelected = { rooms ->
                    selectedRooms = rooms
                    isSelectingRooms = false
                },
                onBackClick = {
                    if (isGyroAvailable) {
                        captureMode = CaptureMode.AUTO_GYRO
                        isSelectingRooms = false
                    } else {
                        onBackClick()
                    }
                },
                modifier = modifier
            )
        } else {
            ManualCapture360Screen(
                rooms = selectedRooms,
                agencyName = agencyName,
                agentPhone = userPhone,
                onRoomStitched = { room, stitchedSphere ->
                    activeRoomForSphere = room
                    activeSphere = stitchedSphere
                },
                onAllRoomsComplete = {
                    Toast.makeText(context, "همه اتاق‌ها ثبت شدند! تور آماده است.", Toast.LENGTH_LONG).show()
                },
                onSelectRoomsClick = {
                    isSelectingRooms = true
                },
                onBackClick = {
                    if (isGyroAvailable) {
                        captureMode = CaptureMode.AUTO_GYRO
                    } else {
                        onBackClick()
                    }
                },
                modifier = modifier
            )
        }
    } else {
        // AUTO_GYRO mode (existing automatic mode with gyroscope)
        PhotoSphereCameraScreen(
            onSphereReady = { stitchedSphere ->
                activeRoomForSphere = null
                activeSphere = stitchedSphere
            },
            onBackClick = onBackClick,
            onSwitchToManual = {
                captureMode = CaptureMode.MANUAL
                isSelectingRooms = true
            },
            modifier = modifier
        )
    }
}
