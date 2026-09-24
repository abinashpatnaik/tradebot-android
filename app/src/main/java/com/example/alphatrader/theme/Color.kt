package com.example.alphatrader.theme

import androidx.compose.ui.graphics.Color

// Refined dark fintech palette — same token names as before so every
// existing screen/component keeps working, values deepened and given more
// separation between elevation steps for a richer, less flat look.
val BgPrimary = Color(0xFF0A0C11)
val BgSurface = Color(0xFF12151D)
val BgSurfaceRaised = Color(0xFF1A1E28)
val BgOverlay = Color(0xFF242836)

val BgPrimaryLight = Color(0xFFF7F8FA)
val BgSurfaceLight = Color(0xFFFFFFFF)
val BgSurfaceRaisedLight = Color(0xFFF0F2F5)
val BgOverlayLight = Color(0xFFE6E9ED)

val BrandGreen = Color(0xFF00E08A)
val BrandGreenDim = Color(0xFF122B22)
val BrandGreenDimLight = Color(0xFFE3F6EC)

val BrandRed = Color(0xFFFF5C5C)
val BrandRedDim = Color(0xFF2E1518)
val BrandRedDimLight = Color(0xFFFDECEC)

val BrandAmber = Color(0xFFFFB020)
val BrandAmberDim = Color(0xFF332508)
val BrandAmberDimLight = Color(0xFFFFF6E0)

val BrandBlue = Color(0xFF4DA3FF)
val BrandBlueDim = Color(0xFF122335)
val BrandBlueDimLight = Color(0xFFE7F1FF)

val BrandPurple = Color(0xFF8B7BFF)

val TextPrimary = Color(0xFFF5F6FA)
val TextSecondary = Color(0xFF8E93A8)
val TextDisabled = Color(0xFF454A5C)

val TextPrimaryLight = Color(0xFF14161C)
val TextSecondaryLight = Color(0xFF676C7E)
val TextDisabledLight = Color(0xFFAEB2BE)

val BorderSubtle = Color(0xFF242836)
val BorderSubtleLight = Color(0xFFE1E4E9)

val StatusLive = BrandGreen
val StatusClosed = BrandRed
val StatusSleeping = TextSecondary

// Position protection status — distinct from generic P&L green/red so
// "am I protected" reads as its own signal, not just another win/loss color.
val ProtectionArmed = BrandBlue      // trailing stop active, profit locked
val ProtectionArmedDim = BrandBlueDim
val ProtectionBase = BrandAmber      // only the original hard stop protects this
val ProtectionBaseDim = BrandAmberDim
