package org.example.project.platform

import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLDistanceFilterNone
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.darwin.NSObject

/**
 * Implementare iOS a [LocationService] folosind CLLocationManager.
 *
 * ⚠️ Premisă: cheile de permisiune trebuie adăugate în Info.plist:
 * ```xml
 * <key>NSLocationWhenInUseUsageDescription</key>
 * <string>SyncSafe are nevoie de locație pentru a arăta membrii grupului pe hartă.</string>
 * ```
 */
class IosLocationService : LocationService {

    private val locationManager = CLLocationManager()
    private var delegate: LocationDelegate? = null

    override fun startTracking(onLocation: (Double, Double) -> Unit) {
        val d = LocationDelegate(onLocation)
        delegate = d

        locationManager.delegate = d
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter = kCLDistanceFilterNone
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    override fun stopTracking() {
        locationManager.stopUpdatingLocation()
        locationManager.delegate = null
        delegate = null
    }

    /** Delegate CLLocationManager — primește evenimentele de locație. */
    private class LocationDelegate(
        private val onLocation: (Double, Double) -> Unit
    ) : NSObject(), CLLocationManagerDelegateProtocol {

        override fun locationManager(
            manager: CLLocationManager,
            didUpdateLocations: List<*>
        ) {
            val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
            onLocation(
                location.coordinate.useContents { latitude },
                location.coordinate.useContents { longitude }
            )
        }
    }
}
