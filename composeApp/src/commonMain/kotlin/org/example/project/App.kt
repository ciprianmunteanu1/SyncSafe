package org.example.project

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.data.ThemeManager
import org.example.project.ui.components.Navigation
import org.example.project.ui.theme.SyncSafeTheme

@Composable
fun App() {
    val systemDark = isSystemInDarkTheme()

    // Initialise from persisted preference
    var darkModeOverride by remember {
        mutableStateOf(ThemeManager.getDarkModeOverride())
    }

    val isDark = darkModeOverride ?: systemDark

    SyncSafeTheme(darkTheme = isDark) {
        Navigation(
            isDarkMode = isDark,
            onToggleDarkMode = { enabled ->
                ThemeManager.setDarkMode(enabled)
                darkModeOverride = enabled
            }
        )
    }
}