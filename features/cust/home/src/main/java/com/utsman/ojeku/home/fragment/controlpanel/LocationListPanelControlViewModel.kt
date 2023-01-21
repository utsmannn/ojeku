package com.utsman.ojeku.home.fragment.controlpanel

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

    val currentLocation
        get() = runBlocking { locationManager.getLocationFlow().first() }

    val locationListState
        get() = locationListRepository.locationListState.asLiveData(viewModelScope.coroutineContext)

    fun getSavedLocation() = viewModelScope.launch {
        locationListRepository.getSavedLocationList()
    }
}