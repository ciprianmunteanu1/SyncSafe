package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.GroupRepository
import org.example.project.model.MeetingPoint
import org.example.project.platform.LocationService

/**
 * ViewModel pentru hartă, locație și meeting point.
 *
 * Primește un [LocationService] injectat din exterior (Android sau iOS),
 * astfel încât commonMain nu depinde de nicio implementare specifică platformei.
 *
 * **Cum se folosește locația:**
 * - Pe Android: FusedLocationProviderClient (GPS + WiFi + Cell)
 * - Pe iOS: CLLocationManager
 * - [locationService] este setat din MainActivity (Android) sau din iOSApp (iOS)
 *
 * Fluxul complet:
 * ```
 * GPS hardware → LocationService → MapViewModel → GroupRepository → Firebase → alte device-uri
 * ```
 */
class MapViewModel : ViewModel() {

    /** Grupul curent — pentru afișarea membrilor și a locațiilor pe hartă. */
    val group: StateFlow<org.example.project.model.Group?> = GroupRepository.group

    /**
     * Serviciul de locație specific platformei.
     * Setat din MainActivity pe Android sau din iOSApp pe iOS.
     *
     * Exemplu Android (în MainActivity.kt):
     * ```kotlin
     * val mapViewModel: MapViewModel by viewModels()
     * mapViewModel.locationService = AndroidLocationService(this)
     * ```
     */
    var locationService: LocationService? = null

    // ─── Locație ──────────────────────────────────────────────────────────────

    /**
     * Pornește urmărirea GPS. La fiecare actualizare semnificativă de locație,
     * coordonatele sunt trimise la Firebase și devin vizibile celorlalți membri.
     *
     * ⚠️ Asigură-te că permisiunile de locație sunt acordate înainte de apel.
     */
    fun startLocationTracking() {
        locationService?.startTracking { lat, lng ->
            viewModelScope.launch {
                GroupRepository.updateMyLocation(lat, lng)
            }
        }
    }

    /** Oprește urmărirea GPS. Apelat când utilizatorul iese din ecranul de hartă. */
    fun stopLocationTracking() {
        locationService?.stopTracking()
    }

    // ─── Meeting Point ────────────────────────────────────────────────────────

    /**
     * Setează punctul de întâlnire al grupului.
     * Toți membrii vor vedea noul punct pe hartă în max 3 secunde.
     *
     * @param latitude  Latitudinea punctului (grade zecimale).
     * @param longitude Longitudinea punctului (grade zecimale).
     * @param name      Denumire descriptivă (ex: "Intrarea Parcului Central").
     */
    fun setMeetingPoint(latitude: Double, longitude: Double, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            GroupRepository.setMeetingPoint(
                MeetingPoint(latitude = latitude, longitude = longitude, name = name)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationTracking()
    }
}
