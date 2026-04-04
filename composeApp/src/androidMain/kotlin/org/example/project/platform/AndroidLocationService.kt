package org.example.project.platform

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Implementare Android a [LocationService] folosind FusedLocationProviderClient.
 *
 * FusedLocationProvider combină GPS, WiFi și rețeaua celulară pentru
 * a oferi cea mai precisă locație cu consum minim de baterie.
 *
 * ⚠️ Premisă: permisiunile ACCESS_FINE_LOCATION sau ACCESS_COARSE_LOCATION
 * trebuie să fie acordate de utilizator înainte de a apela [startTracking].
 * Verificarea permisiunilor se face în UI (MainActivity sau Composable).
 *
 * Utilizare din MainActivity:
 * ```kotlin
 * val mapViewModel: MapViewModel by viewModels()
 * mapViewModel.locationService = AndroidLocationService(this)
 * mapViewModel.startLocationTracking()
 * ```
 *
 * @param context Context Android necesar pentru FusedLocationProviderClient.
 */
class AndroidLocationService(context: Context) : LocationService {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    /**
     * Configurarea cererilor de locație:
     * - Interval: 5 secunde (echilibru precizie / baterie)
     * - Prioritate: HIGH_ACCURACY (GPS activ)
     * - Distanță minimă: actualizare la orice schimbare (0m)
     */
    private fun buildLocationRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
            .setMinUpdateIntervalMillis(3_000L)
            .build()

    @SuppressLint("MissingPermission")
    override fun startTracking(onLocation: (Double, Double) -> Unit) {
        // Oprim orice tracking anterior
        stopTracking()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    onLocation(location.latitude, location.longitude)
                }
            }
        }

        fusedClient.requestLocationUpdates(
            buildLocationRequest(),
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    override fun stopTracking() {
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
        locationCallback = null
    }
}
