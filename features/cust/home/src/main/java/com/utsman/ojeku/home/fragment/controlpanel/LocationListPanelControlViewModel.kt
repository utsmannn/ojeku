package com.utsman.ojeku.home.fragment.controlpanel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.core.LocationManager
import com.utsman.ojeku.home.repo.LocationListRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LocationListPanelControlViewModel(
    private val locationListRepository: LocationListRepository,
    private val locationManager: LocationManager
) : ViewModel() {

    private var _currentLocation: Location = Location("")
    val currentLocation
        get() = _currentLocation

    init {
        locationManager.getLastLocation {
            _currentLocation = it
        }
    }

    val locationListState
        get() = locationListRepository.locationListState.asLiveData(viewModelScope.coroutineContext)

    fun getSavedLocation() = viewModelScope.launch {
        locationListRepository.getSavedLocationList()
    }
}