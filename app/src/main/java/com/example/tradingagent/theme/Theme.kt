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

private val DarkScheme = darkColorScheme(
    primary = WfAccent,
    onPrimary = Color.White,
    primaryContainer = WfAccentMuted,
    onPrimaryContainer = WfAccent,

    secondary = WfTextSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF424242),
    onSecondaryContainer = Color(0xFFE0E0E0),

    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = Color(0xFFBDBDBD),

    outline = Color(0xFF757575),
    outlineVariant = Color(0xFF424242),
)

@Composable
fun TradingAgentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkScheme
        else -> WireframeScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
