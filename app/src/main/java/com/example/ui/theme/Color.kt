package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Required Palette (Section 10)
val DarkBackground = Color(0xFF121212) // Background #121212
val DarkSurface = Color(0xFF1E1E1E) // Surface #1E1E1E
val DarkSurfaceCard = Color(0xFF252525)
val DarkDivider = Color(0xFF2C2C2C) // Divider #2C2C2C

val BrandPrimary = Color(0xFF0D47A1) // Primary #0D47A1 (dark blue)
val BrandPrimaryLight = Color(0xFF1976D2) // Primary Variant #1976D2
val BrandPrimaryContainer = Color(0xFF1A3B70)
val BrandOnPrimary = Color(0xFFFFFFFF)

val BrandSecondary = Color(0xFF388E3C) // Secondary #388E3C (green)
val BrandSecondaryLight = Color(0xFF4CAF50)
val BrandSecondaryContainer = Color(0xFF1B4D20)
val BrandOnSecondary = Color(0xFFFFFFFF)

val AccentYellow = Color(0xFFFFC107) // Accent Yellow #FFC107 (buttons, highlights)
val AccentOrange = Color(0xFFFF5722) // Accent Orange #FF5722 (ads, warnings)
val BrandGold = AccentYellow

val ErrorRed = Color(0xFFD32F2F) // Error #D32F2F

val TextPrimary = Color(0xFFFFFFFF) // Text Primary #FFFFFF
val TextSecondary = Color(0xFFB0B0B0) // Text Secondary #B0B0B0
val TextMuted = Color(0xFF757575)

// Dashboard grid specific colors
val ColorTour = Color(0xFF1976D2)
val ColorProperties = Color(0xFF388E3C)
val ColorSubscription = Color(0xFFF57C00)
val ColorAds = Color(0xFFFF5722)
val ColorAnalytics = Color(0xFF9C27B0)
val ColorProfile = Color(0xFF607D8B)

// 360 Photo Sphere Camera & HUD Colors
val SphereAccent = Color(0xFF4CAF50) // Aligned, captured, ready
val SphereActive = AccentYellow // Live aim target
val GlassSurface = Color(0xFF000000).copy(alpha = 0.55f)
val GlassSurfaceDim = Color(0xFF000000).copy(alpha = 0.38f)
val GlassContent = Color(0xFFF2F5F7)
val GlassContentDim = Color(0xFFF2F5F7).copy(alpha = 0.62f)
val ChromeScrim = Color(0xFF000000).copy(alpha = 0.60f)
val SphereBackground = DarkBackground
val SphereSurface = DarkSurface
val SphereSurfaceHigh = DarkSurfaceCard
val SphereOnSurface = TextPrimary
val SphereOnSurfaceVariant = TextSecondary
val SphereOutline = Color(0xFF333D45)
val PhotoWell = Color(0xFF05070A)

val PillShape = androidx.compose.foundation.shape.RoundedCornerShape(percent = 50)

