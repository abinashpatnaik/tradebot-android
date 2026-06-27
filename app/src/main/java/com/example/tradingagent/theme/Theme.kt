package com.example.tradingagent.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Low-Fidelity Wireframe Scheme ──────────────────────
private val WireframeScheme = lightColorScheme(
    primary = WfAccent,
    onPrimary = Color.White,
    primaryContainer = WfAccentMuted,
    onPrimaryContainer = WfAccent,

    secondary = WfTextSecondary,
    onSecondary = Color.White,
    secondaryContainer = WfSurfaceVariant,
    onSecondaryContainer = WfTextPrimary,

    tertiary = WfTextSecondary,
    onTertiary = Color.White,
    tertiaryContainer = WfSurfaceVariant,
    onTertiaryContainer = WfTextPrimary,

    background = WfBackground,
    onBackground = WfTextPrimary,

    surface = WfSurface,
    onSurface = WfTextPrimary,
    surfaceVariant = WfSurfaceVariant,
    onSurfaceVariant = WfTextSecondary,

    outline = WfTextSecondary,
    outlineVariant = WfSurfaceVariant,

    inverseSurface = WfTextPrimary,
    inverseOnSurface = WfBackground,
    inversePrimary = WfAccentMuted,
)

@Composable
fun TradingAgentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = WireframeScheme, typography = Typography, content = content)
}
