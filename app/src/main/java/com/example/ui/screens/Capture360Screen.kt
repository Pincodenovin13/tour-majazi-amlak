package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.camera.PhotoSphereCameraScreen
import com.example.data.AppRepository
import com.example.model.TourScene
import com.example.result.PanoramaResultScreen
import com.example.storage.SphereImageStore.StitchedSphere

/**
 * Capture360Screen: Real CameraX + Gyroscope + OpenCV stitching 360 photo sphere capture screen.
 * Replaces the simulated implementation with the full native photosphere pipeline.
 */
@Composable
fun Capture360Screen(
    repository: AppRepository,
    onCaptureFinished: (TourScene) -> Unit = {},
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSphere by remember { mutableStateOf<StitchedSphere?>(null) }

    val sphere = activeSphere
    if (sphere != null) {
        PanoramaResultScreen(
            sphere = sphere,
            onTakeAnother = {
                activeSphere = null
            },
            onSaveToTour = { scene ->
                repository.addStitchedScene(scene)
                onCaptureFinished(scene)
            },
            onBackClick = {
                activeSphere = null
            },
            modifier = modifier
        )
    } else {
        PhotoSphereCameraScreen(
            onSphereReady = { stitchedSphere ->
                activeSphere = stitchedSphere
            },
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}
