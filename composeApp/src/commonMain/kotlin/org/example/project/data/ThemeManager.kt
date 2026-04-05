package org.example.project.data

import com.russhwolf.settings.Settings

/**
 * Manages the user's theme preference (dark/light mode).
 * Persists using multiplatform-settings (SharedPreferences / NSUserDefaults).
 */
object ThemeManager {

    private val settings = Settings()

    private const val KEY_DARK_MODE = "dark_mode_enabled"
    private const val KEY_USE_SYSTEM = "use_system_theme"

    /** Returns the saved dark mode preference, or null if "follow system" is set. */
    fun getDarkModeOverride(): Boolean? {
        if (settings.getBoolean(KEY_USE_SYSTEM, true)) return null
        return settings.getBoolean(KEY_DARK_MODE, true)
    }

    /** Sets explicit dark mode (disables system follow). */
    fun setDarkMode(enabled: Boolean) {
        settings.putBoolean(KEY_USE_SYSTEM, false)
        settings.putBoolean(KEY_DARK_MODE, enabled)
    }

    /** Resets to follow system theme. */
    fun useSystemTheme() {
        settings.putBoolean(KEY_USE_SYSTEM, true)
    }

    /** Returns true if "follow system" is active. */
    fun isUsingSystemTheme(): Boolean {
        return settings.getBoolean(KEY_USE_SYSTEM, true)
    }
}
