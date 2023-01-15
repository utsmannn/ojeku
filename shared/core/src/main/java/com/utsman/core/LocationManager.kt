package com.utsman.core

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.utsman.core.extensions.value
import com.utsman.core.state.StateEvent
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationManager(private val context: Context) {

    private val fusedLocationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = 1000
    }

    @SuppressLint("MissingPermission")
    fun getLocationFlow(): Flow<Location> {
        val callbackFlow = callbackFlow<Location> {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    for (location in result.locations) {
                        trySend(location)
                    }
                }
            }

            fusedLocationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnCanceledListener {
                cancel("Canceled by user")
            }.addOnFailureListener {
                cancel("Get location failure", it)
            }

            awaitClose { fusedLocationProvider.removeLocationUpdates(locationCallback) }
        }

        return callbackFlow.distinctUntilChanged { old, new ->
            old.distanceTo(new) < 10f
        }
    }

    fun getLocationFlowEvent(): Flow<StateEvent<Location>> {
        val callbackFlow = callbackFlow<StateEvent<Location>> {
            trySendBlocking(StateEvent.Loading())
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    for (location in result.locations) {
                        val stateSuccess = StateEvent.Success(location)
                        println(" --- in flow ===> $location")
                        trySendBlocking(stateSuccess)
                    }
                }
            }

            fusedLocationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnCanceledListener {
                cancel("Canceled by user")
                trySendBlocking(StateEvent.Failure(IllegalStateException("Canceled by user")))
            }.addOnFailureListener {
                cancel("Get location failure", it)
                trySendBlocking(StateEvent.Failure(it))
            }

            awaitClose { fusedLocationProvider.removeLocationUpdates(locationCallback) }
        }

        return callbackFlow.distinctUntilChanged { old, new ->
            val distance = old.value?.distanceTo(new.value) ?: 0f
            distance < 10f
        }
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(lastLocation: (Location) -> Unit) {
        val lastLocationRequest = LastLocationRequest.Builder()
            .build()

        fusedLocationProvider.getLastLocation(lastLocationRequest)
            .addOnFailureListener {
                it.printStackTrace()
            }
            .addOnSuccessListener {
                println("AAAAA -> $it")
                lastLocation.invoke(it)
            }
    }

    companion object : KoinComponent {
        val instance: LocationManager by inject()
    }
}