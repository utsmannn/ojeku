package com.utsman.ojeku.home.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.launch

class EditLocationViewModel(
    private val editLocationRepository: EditLocationRepository
) : ViewModel() {

    val editState =
        editLocationRepository.editLocationState.asLiveData(viewModelScope.coroutineContext)


    var savedLocation = LocationData()

    fun editLocationData() = viewModelScope.launch {
        println("asuuuuu edit -> ${savedLocation.name}")
        if (savedLocation != LocationData()) {
            editLocationRepository.editLocation(savedLocation)
        }
    }
}