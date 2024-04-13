package com.example.simpleweather

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import kotlinx.coroutines.launch

/**
 * Service object responsible for handling location-related functionality.
 */
object LocationService {
    private const val REQUEST_CODE_PERMISSION = 100

    /**
     * Requests the location permission from the user.
     * @param activity The activity where the permission is requested.
     */
    fun requestLocationPermission(activity: ComponentActivity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initializeLocationUpdates(activity)
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    /**
     * Initializes location updates using the FusedLocationProviderClient.
     * @param activity The activity where the location updates are initialized.
     */
    private fun initializeLocationUpdates(activity: ComponentActivity) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 seconds
            fastestInterval = 5000 // 5 seconds
        }

        val coroutineScope = activity.lifecycleScope

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult?.lastLocation?.let { location ->
                            coroutineScope.launch {
                                MainActivity.currentLocation = Pair(location.latitude, location.longitude)
                            }
                        }
                    }
                },
                null
            )
        } catch (e: SecurityException) {
            // Handle permission-related issues
        }

    }

    /**
     * Gets the last known location using the FusedLocationProviderClient.
     * @param fusedLocationClient The FusedLocationProviderClient instance.
     * @param locationRequest The LocationRequest instance.
     */
    fun getLastKnownLocation(fusedLocationClient: FusedLocationProviderClient, locationRequest: LocationRequest) {

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult?.lastLocation?.let { location ->
                            MainActivity.currentLocation = Pair(location.latitude, location.longitude)

                        }
                    }
                },
                null
            )
        } catch (e: SecurityException) {
            // Handle permission-related issues
        }
    }

}

/**
 * Calculates the Euclidean distance between two geographical points.
 * @param lat1 Latitude of the first point.
 * @param lon1 Longitude of the first point.
 * @param lat2 Latitude of the second point.
 * @param lon2 Longitude of the second point.
 * @return Euclidean distance between the points.
 */
fun calculateEuclideanDistanceForAirQuality(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val latDiff = lat2 - lat1
    val lonDiff = lon2 - lon1
    val distance = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff)
    return distance
}

/**
 * Retrieves latitude and longitude coordinates based on province and city codes.
 * @param provinceCode Code of the province.
 * @param index Index of the city.
 * @return Pair containing latitude and longitude coordinates.
 */
fun getLatLonByCityCode(provinceCode: Int, index: Int): Pair<Double, Double>? {
    return when (provinceCode) {
        0 -> when (index) {
            0 -> Pair(35.6895, 51.3890) // Tehran
            1 -> Pair(35.7590, 52.7755) // Firuzkuh
            2 -> Pair(35.7013, 52.0586) // Damavand
            else -> null
        }
        1 -> when (index) {
            0 -> Pair(31.3183, 48.6713) // Ahwaz
            1 -> Pair(30.3472, 48.3043) // Abadan
            2 -> Pair(32.3852, 48.4239) // Dezful
            else -> null
        }
        2 -> when (index) {
            0 -> Pair(36.5669, 53.0588) // Sari
            1 -> Pair(36.9020, 50.6571) // Ramsar
            2 -> Pair(36.6580, 51.4225) // Chalus
            else -> null
        }
        3 -> when (index) {
            0 -> Pair(29.5845, 52.5371) // Shiraz
            1 -> Pair(30.1146, 51.5284) // Nurabad
            2 -> Pair(28.5049, 53.5798) // Jahrom
            else -> null
        }
        4 -> when (index) {
            0 -> Pair(34.0917, 49.6892) // Arak
            1 -> Pair(33.6361, 50.0784) // Khomein
            2 -> Pair(33.9112, 50.4551) // Mahalat
            else -> null
        }
        else -> null
    }
}
