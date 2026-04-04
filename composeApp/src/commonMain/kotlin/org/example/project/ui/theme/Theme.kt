package org.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkBackground = Color(0xFF0D0D1A)
val CyanAccent = Color(0xFF00E5FF)
val SafeGreen = Color(0xFF4CAF50)
val EmergencyRed = Color(0xFFFF5252)
val UnknownOrange = Color(0xFFFF9800)
val OnTheWayBlue = Color(0xFF2196F3)

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    background = DarkBackground,
    surface = DarkBackground,
    error = EmergencyRed,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyanAccent,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    error = EmergencyRed,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

@Composable
fun SyncSafeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
