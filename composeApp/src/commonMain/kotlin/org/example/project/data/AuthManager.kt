package org.example.project.data

import com.russhwolf.settings.Settings

/**
 * Gestionează sesiunea locală a utilizatorului pe dispozitiv (reține datele contului centralizat).
 * Folosește [Settings] pentru a salva cheile în SharedPreferences (Android) 
 * sau NSUserDefaults (iOS).
 */
object AuthManager {

    private val settings = Settings()

    private const val KEY_USERNAME = "user_name_v2"
    private const val KEY_PASSWORD = "password_v2"

    fun getUsername(): String? = settings.getStringOrNull(KEY_USERNAME)

    fun getPassword(): String? = settings.getStringOrNull(KEY_PASSWORD)

    /**
     * Salvează datele sesiunii curente permanent pe device.
     */
    fun saveSession(username: String, passwordHash: String) {
        settings.putString(KEY_USERNAME, username)
        settings.putString(KEY_PASSWORD, passwordHash)
    }

    /**
     * Șterge sesiunea (Log Out).
     */
    fun clearSession() {
        settings.remove(KEY_USERNAME)
        settings.remove(KEY_PASSWORD)
    }

    /**
     * Returnează true dacă avem o sesiune validă salvată.
     */
    fun hasValidSession(): Boolean {
        return getUsername() != null && getPassword() != null
    }
}
