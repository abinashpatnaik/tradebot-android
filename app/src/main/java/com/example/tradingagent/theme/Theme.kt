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

// ── Alpha Trader Light Scheme ──────────────────────
private val AlphaLightScheme = lightColorScheme(
    primary = LightPrimaryAccent,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryAccent.copy(alpha = 0.1f),
    onPrimaryContainer = LightPrimaryAccent,

    secondary = LightSecondaryAccent,
    onSecondary = Color.White,
    secondaryContainer = LightSecondaryAccent.copy(alpha = 0.1f),
    onSecondaryContainer = LightSecondaryAccent,

    tertiary = LightTextSecondary,
    onTertiary = Color.White,
    tertiaryContainer = LightCardBorder,
    onTertiaryContainer = LightTextPrimary,

    background = LightBackground,
    onBackground = LightTextPrimary,

    surface = LightCardBg,
    onSurface = LightTextPrimary,
    surfaceVariant = LightBackground,
    onSurfaceVariant = LightTextSecondary,

    outline = LightCardBorder,
    outlineVariant = LightCardBorder.copy(alpha = 0.5f),

    inverseSurface = DarkCardBg,
    inverseOnSurface = DarkTextPrimary,
    inversePrimary = DarkPrimaryAccent,
)

// ── Alpha Trader Dark Scheme ──────────────────────
private val AlphaDarkScheme = darkColorScheme(
    primary = DarkPrimaryAccent,
    onPrimary = Color.Black,
    primaryContainer = DarkPrimaryAccent.copy(alpha = 0.1f),
    onPrimaryContainer = DarkPrimaryAccent,

    secondary = DarkSecondaryAccent,
    onSecondary = Color.Black,
    secondaryContainer = DarkSecondaryAccent.copy(alpha = 0.1f),
    onSecondaryContainer = DarkSecondaryAccent,

    tertiary = DarkTextSecondary,
    onTertiary = Color.Black,
    tertiaryContainer = DarkCardBorder,
    onTertiaryContainer = DarkTextPrimary,

    background = DarkBackground,
    onBackground = DarkTextPrimary,

    surface = DarkCardBg,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkBackground,
    onSurfaceVariant = DarkTextSecondary,

    outline = DarkCardBorder,
    outlineVariant = DarkCardBorder.copy(alpha = 0.5f),

    inverseSurface = LightCardBg,
    inverseOnSurface = LightTextPrimary,
    inversePrimary = LightPrimaryAccent,
)

@Composable
fun TradingAgentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force brand colors by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> AlphaDarkScheme
        else -> AlphaLightScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
