package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import kotlin.math.max

/**
 * Watermarking utility for real estate agent photos and 360 panoramas.
 * Adds the agency name and phone number with clean high-contrast styling and 70% opacity.
 */
object WatermarkUtils {

    /**
     * Draws the agency watermark on a Bitmap:
     * - Bottom-right corner (or customizable positioning)
     * - Agency name + Agent phone number: "[نام آژانس] - [شماره موبایل]"
     * - Semi-transparent (~70% opacity)
     * - White text with black shadow for maximum legibility on any scene background
     */
    fun applyAgentWatermark(
        srcBitmap: Bitmap,
        agencyName: String,
        agentPhone: String
    ): Bitmap {
        val workingAgency = agencyName.ifBlank { "املاک مدرن شمیران" }
        val workingPhone = agentPhone.ifBlank { "۰۹۱۲۳۴۵۶۷۸۹" }
        val watermarkText = "$workingAgency - $workingPhone"

        // Create mutable copy if srcBitmap is not mutable
        val mutableBitmap = if (srcBitmap.isMutable) {
            srcBitmap
        } else {
            srcBitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val canvas = Canvas(mutableBitmap)
        val width = mutableBitmap.width
        val height = mutableBitmap.height

        // Calculate responsive font size based on image width (approx 18sp equivalent)
        val fontSize = max(24f, width * 0.016f)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = (255 * 0.70f).toInt() // 70% opacity
            textSize = fontSize
            typeface = Typeface.DEFAULT_BOLD
            setShadowLayer(fontSize * 0.25f, 2f, 2f, Color.argb(180, 0, 0, 0))
        }

        // Measure text bounds
        val bounds = Rect()
        textPaint.getTextBounds(watermarkText, 0, watermarkText.length, bounds)

        val padding = fontSize * 1.2f
        // Draw at bottom-right corner (or in RTL perspective, right-aligned)
        val x = (width - bounds.width() - padding).coerceAtLeast(padding)
        val y = height - padding

        canvas.drawText(watermarkText, x, y, textPaint)
        return mutableBitmap
    }
}
