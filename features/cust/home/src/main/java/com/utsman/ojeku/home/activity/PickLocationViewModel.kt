package com.utsman.ojeku.home.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.utsman.locationapi.entity.LocationData
import com.utsman.ojeku.cust.search.SearchLocationRepository
import kotlinx.coroutines.launch

class PickLocationViewModel(
    private val searchLocationRepository: SearchLocationRepository
) : ViewModel() {

    val locationState = searchLocationRepository.locationState.asLiveData(viewModelScope.coroutineContext)

    var pickDataLocation = LocationData()

    fun searchLocation(query: String, coordinate: LatLng) = viewModelScope.launch {
        val coordinateString = "${coordinate.latitude},${coordinate.longitude}"
        searchLocationRepository.searchLocation(query, coordinateString)
    }

    fun clearState() = viewModelScope.launch {
        searchLocationRepository.clearLocationState()
    }
}