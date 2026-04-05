package org.example.project

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
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

    // Urmărim cel mai recent timestamp ca să nu bâzâie din cauza alertelor vechi la startup
    var lastAlertTimestamp by remember { 
        mutableStateOf(kotlin.time.Clock.System.now().toEpochMilliseconds()) 
    }

    LaunchedEffect(Unit) {
        org.example.project.data.GroupRepository.alerts.collect { alerts ->
            val me = org.example.project.data.GroupRepository.me
            val newAlerts = alerts.filter { it.timestamp > lastAlertTimestamp }
            
            if (newAlerts.isNotEmpty()) {
                lastAlertTimestamp = newAlerts.maxOf { it.timestamp }
                
                // Dacă e o alertă nouă de SOS și NU e emisă de mine
                val hasSOS = newAlerts.any { it.type == org.example.project.model.AlertType.NEEDS_HELP && it.memberId != me?.id }
                
                if (hasSOS) {
                    org.example.project.platform.EmergencyHardware().triggerSOSAlarm()
                }
            }
        }
    }

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