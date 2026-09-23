package com.example.civfix.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation(
        context: Context,
        onLocationFetched: (Double, Double, String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val cancellationTokenSource = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                val readableAddress = "Lat: %.4f, Lng: %.4f".format(lat, lng)
                onLocationFetched(lat, lng, readableAddress)
            } else {
                // Emulator fallback if no active GPS fix is pushed
                val fallbackLat = 45.5152
                val fallbackLng = -122.6784
                onLocationFetched(
                    fallbackLat,
                    fallbackLng,
                    "Lat: 45.5152, Lng: -122.6784 (Emulator GPS Mock)"
                )
            }
        }.addOnFailureListener {
            // Safe emulator fallback on failure
            onLocationFetched(
                45.5152,
                -122.6784,
                "Lat: 45.5152, Lng: -122.6784 (Emulator GPS Mock)"
            )
        }
    }
}