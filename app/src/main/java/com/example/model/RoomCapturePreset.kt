package com.example.model

/**
 * Capture mode:
 * AUTO_GYRO: Automatic capture guided by device gyroscope/rotation vector sensor
 * MANUAL: Step-by-step manual capture for devices without gyroscope or user preference
 */
enum class CaptureMode {
    AUTO_GYRO,
    MANUAL
}

/**
 * Room preset plan for 360 photo tours.
 * Different room sizes need different numbers of photos for 360 coverage.
 */
data class RoomCapturePreset(
    val id: String,
    val persianName: String,
    val englishName: String,
    val photoCount: Int,
    val notes: String = "",
    val isCustom: Boolean = false
) {
    companion object {
        val DEFAULT_PRESETS = listOf(
            RoomCapturePreset("living_room", "پذیرایی", "Living Room", 8, "اتاق اصلی"),
            RoomCapturePreset("kitchen", "آشپزخانه", "Kitchen", 8),
            RoomCapturePreset("bedroom_1", "اتاق خواب اول", "Bedroom 1", 8),
            RoomCapturePreset("bedroom_2", "اتاق خواب دوم", "Bedroom 2", 8),
            RoomCapturePreset("bedroom_3", "اتاق خواب سوم", "Bedroom 3", 8),
            RoomCapturePreset("bathroom", "حمام", "Bathroom", 6, "فضای کوچک‌تر"),
            RoomCapturePreset("toilet", "دستشویی", "Toilet", 6, "فضای کوچک‌تر"),
            RoomCapturePreset("parking", "پارکینگ", "Parking", 8),
            RoomCapturePreset("storage", "انباری", "Storage", 6),
            RoomCapturePreset("terrace", "تراس", "Terrace", 8),
            RoomCapturePreset("balcony", "بالکن", "Balcony", 6),
            RoomCapturePreset("roof_garden", "روف گاردن", "Roof Garden", 8),
            RoomCapturePreset("lobby", "لابی", "Lobby", 8),
            RoomCapturePreset("yard", "حیاط", "Yard", 8),
            RoomCapturePreset("pool", "استخر", "Pool", 8)
        )
    }
}
