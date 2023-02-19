package com.utsman.ojeku.home.fragment.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.ojeku.home.repo.LocationListRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val locationListRepository: LocationListRepository
) : ViewModel() {

    val userState = profileRepository.userState.asLiveData(viewModelScope.coroutineContext)

    fun getUser() = viewModelScope.launch {
        profileRepository.getUser()
    }

    val locationListState
        get() = locationListRepository.locationListState.asLiveData(viewModelScope.coroutineContext)

    fun getSavedLocation() = viewModelScope.launch {
        locationListRepository.getSavedLocationList()
    }
}