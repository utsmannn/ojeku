package com.utsman.driver.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val locationResult = homeRepository.locationResult.asLiveData(viewModelScope.coroutineContext)
    val currentUser = profileRepository.userState.asLiveData(viewModelScope.coroutineContext)

    fun getLocation() = viewModelScope.launch {
        homeRepository.getLocation()
    }

    fun getUser() = viewModelScope.launch {
        profileRepository.getUser()
    }

    fun updateDriverActive(isActive: Boolean) = viewModelScope.launch {
        profileRepository.updateDriverActive(isActive)
    }

    fun updateLocation(location: Location) = viewModelScope.launch {
        profileRepository.updateUserLocation(location)
    }
}