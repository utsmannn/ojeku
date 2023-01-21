package com.utsman.ojeku.home.fragment.controlpanel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.locationapi.entity.LocationData
import com.utsman.ojeku.home.repo.HomeRepository
import com.utsman.ojeku.home.repo.LocationListRepository
import kotlinx.coroutines.launch

class LocationListPanelControlViewModel(
    private val locationListRepository: LocationListRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    val locationListState
        get() = locationListRepository.locationListState.asLiveData(viewModelScope.coroutineContext)

    fun getSavedLocation() = viewModelScope.launch {
        locationListRepository.getSavedLocationList()
    }

    fun setLocationDest(locationData: LocationData) = viewModelScope.launch {
        homeRepository.setDestinationLocation(locationData)
    }
}