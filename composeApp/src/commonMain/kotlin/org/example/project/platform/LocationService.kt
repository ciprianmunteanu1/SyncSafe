package org.example.project.platform

/**
 * Interfață de locație cross-platform.
 *
 * Definită în commonMain ca interfață simplă (nu expect/actual),
 * astfel fiecare platformă poate avea propriul constructor.
 *
 * Implementări:
 * - Android → [AndroidLocationService] (androidMain) — FusedLocationProviderClient
 * - iOS    → [IosLocationService] (iosMain) — CLLocationManager
 *
 * Injectată în [MapViewModel] din codul specific platformei.
 */
interface LocationService {

    /**
     * Pornește urmărirea GPS continuă.
     * [onLocation] este apelat la fiecare actualizare semnificativă de poziție.
     *
     * @param onLocation Callback cu (latitude, longitude) în grade zecimale.
     */
    fun startTracking(onLocation: (latitude: Double, longitude: Double) -> Unit)

    /**
     * Oprește urmărirea GPS și eliberează resursele.
     * Apelat din [MapViewModel.onCleared] sau când utilizatorul revocă permisiunile.
     */
    fun stopTracking()
}
