package com.example.tradingagent.theme

import androidx.compose.ui.graphics.Color

// ── Alpha Trader Dark Palette ────────────────
val DarkBackground = Color(0xFF0B0B0E)
val DarkCardBg = Color(0xFF14141A)
val DarkCardBorder = Color(0xFF1F1F2E)
val DarkPrimaryAccent = Color(0xFF00E5A3)
val DarkSecondaryAccent = Color(0xFFFF4757)
val DarkTextPrimary = Color(0xFFFFFFFF)
val DarkTextSecondary = Color(0xFF8B8B9E)
val DarkTextTertiary = Color(0xFF5A5A6E)

// ── Alpha Trader Light Palette ────────────────
val LightBackground = Color(0xFFF4F5F7)
val LightCardBg = Color(0xFFFFFFFF)
val LightCardBorder = Color(0xFFE5E7EB)
val LightPrimaryAccent = Color(0xFF00BA88)
val LightSecondaryAccent = Color(0xFFED2E42)
val LightTextPrimary = Color(0xFF212121)
val LightTextSecondary = Color(0xFF6B7280)
val LightTextTertiary = Color(0xFF9CA3AF)

// Legacy compat mappings (re-mapped to Dark as default if needed, or removed if unused, keeping for safety)
val NavyDark = DarkBackground
val NavySurface = DarkCardBg
val NavyCard = DarkCardBg
val NavyCardVariant = DarkCardBorder
val CyanAccent = DarkPrimaryAccent
val TealAccent = DarkPrimaryAccent
val GoldAccent = Color(0xFFFFA502)
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextMuted = DarkTextTertiary

val Purple80 = DarkPrimaryAccent
val PurpleGrey80 = DarkTextSecondary
val Pink80 = DarkPrimaryAccent
val Purple40 = LightPrimaryAccent
val PurpleGrey40 = LightTextSecondary
val Pink40 = LightPrimaryAccent
