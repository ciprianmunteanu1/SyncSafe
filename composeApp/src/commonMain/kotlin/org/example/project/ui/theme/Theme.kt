package org.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ── Semantic palette (shared across modes) ──
val SafeGreen = Color(0xFF4CAF50)
val EmergencyRed = Color(0xFFFF5252)
val UnknownGray = Color(0xFF9E9E9E)
val OnTheWayBlue = Color(0xFF2196F3)
val UnknownOrange = Color(0xFFFF9800)
val AccentPurple = Color(0xFF9C27B0)

// ── Dark Mode ──
private val DarkCyanAccent = Color(0xFF00E5FF)
private val DarkBackground = Color(0xFF0D0D1A)
private val DarkSurface = Color(0xFF1A1A2E)
private val DarkSurfaceVariant = Color(0xFF252540)

private val DarkColorScheme = darkColorScheme(
    primary = DarkCyanAccent,
    onPrimary = Color.Black,
    secondary = OnTheWayBlue,
    onSecondary = Color.White,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color.White.copy(alpha = 0.7f),
    error = EmergencyRed,
    onError = Color.White,
    outline = Color.White.copy(alpha = 0.3f)
)

// ── Light Mode ──
private val LightCyanAccent = Color(0xFF0097A7)
private val LightBackground = Color(0xFFF5F7FA)
private val LightSurface = Color.White
private val LightSurfaceVariant = Color(0xFFE8EDF2)

private val LightColorScheme = lightColorScheme(
    primary = LightCyanAccent,
    onPrimary = Color.White,
    secondary = Color(0xFF1976D2),
    onSecondary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF1C1C1E),
    surface = LightSurface,
    onSurface = Color(0xFF1C1C1E),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF5A5A5E),
    error = EmergencyRed,
    onError = Color.White,
    outline = Color(0xFFBDBDBD)
)

// ── Auth screen gradients ──
data class SyncSafeExtraColors(
    val authGradient: Brush,
    val authOnGradient: Color,
    val authOnGradientMuted: Color,
    val authInputBorder: Color,
    val authButtonBackground: Color,
    val authButtonForeground: Color,
    val isDark: Boolean
)

val LocalSyncSafeColors = compositionLocalOf {
    SyncSafeExtraColors(
        authGradient = Brush.verticalGradient(listOf(Color.Black, Color.Black)),
        authOnGradient = Color.White,
        authOnGradientMuted = Color.White.copy(alpha = 0.7f),
        authInputBorder = Color.White.copy(alpha = 0.3f),
        authButtonBackground = Color.White,
        authButtonForeground = Color.Black,
        isDark = true
    )
}

private val DarkExtraColors = SyncSafeExtraColors(
    authGradient = Brush.verticalGradient(
        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    ),
    authOnGradient = Color.White,
    authOnGradientMuted = Color.White.copy(alpha = 0.7f),
    authInputBorder = Color.White.copy(alpha = 0.3f),
    authButtonBackground = Color.White,
    authButtonForeground = Color(0xFF0F2027),
    isDark = true
)

private val LightExtraColors = SyncSafeExtraColors(
    authGradient = Brush.verticalGradient(
        listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2), Color(0xFF80DEEA))
    ),
    authOnGradient = Color(0xFF004D40),
    authOnGradientMuted = Color(0xFF004D40).copy(alpha = 0.7f),
    authInputBorder = Color(0xFF004D40).copy(alpha = 0.4f),
    authButtonBackground = Color(0xFF00796B),
    authButtonForeground = Color.White,
    isDark = false
)

@Composable
fun SyncSafeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) DarkExtraColors else LightExtraColors

    androidx.compose.runtime.CompositionLocalProvider(
        LocalSyncSafeColors provides extraColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
