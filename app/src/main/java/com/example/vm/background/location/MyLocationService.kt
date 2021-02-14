package com.example.vm.background.location

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.vm.ui.locationPermission
import com.example.vm.utils.checkIfGranted
import com.google.android.gms.location.*

private const val TAG = "LocationService"

fun createLocationRequest(): LocationRequest {
    return LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
}

class MyLocationService : Service() {

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                _currentLocation.value = location
            }
        }
    }

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        setupFusedLocationClient()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    @SuppressLint("MissingPermission")
    private fun setupFusedLocationClient() {
        if (!checkIfGranted(this, *locationPermission)) {
            stopSelf()
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null)
                    _currentLocation.value = location
            }

        val locationRequest = createLocationRequest()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): MyLocationService = this@MyLocationService
    }
}