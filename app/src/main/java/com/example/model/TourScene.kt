package com.example.model

data class TourHotspot(
    val id: String,
    val title: String,
    val xPercent: Float, // 0.0f .. 1.0f relative to panorama width
    val yPercent: Float, // 0.0f .. 1.0f relative to panorama height
    val targetSceneId: String? = null,
    val infoText: String? = null,
    val pitch: Float = ((yPercent - 0.5f) * -60f),
    val yaw: Float = ((xPercent - 0.5f) * 360f)
)

data class TourScene(
    val id: String,
    val name: String,
    val drawableResId: Int = 0,
    val imagePath: String? = null,
    val hotspots: List<TourHotspot> = emptyList()
)
