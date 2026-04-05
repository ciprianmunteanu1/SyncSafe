package org.example.project.data

import com.russhwolf.settings.Settings
import org.example.project.i18n.AppLanguage

/**
 * Manages the user's language preference.
 * Persists using multiplatform-settings (SharedPreferences / NSUserDefaults).
 */
object LanguageManager {

    private val settings = Settings()

    private const val KEY_LANGUAGE = "app_language"

    /** Returns the saved language preference, defaulting to English. */
    fun getLanguage(): AppLanguage {
        val code = settings.getString(KEY_LANGUAGE, AppLanguage.EN.code)
        return AppLanguage.entries.find { it.code == code } ?: AppLanguage.EN
    }

    /** Saves the language preference. */
    fun setLanguage(language: AppLanguage) {
        settings.putString(KEY_LANGUAGE, language.code)
    }
}
