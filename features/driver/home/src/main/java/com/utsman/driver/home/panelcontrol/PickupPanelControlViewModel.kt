package com.utsman.driver.home.panelcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.core.extensions.value
import com.utsman.driver.home.HomeRepository
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class PickupPanelControlViewModel(
    private val bookingRepository: BookingRepository,
    private val profileRepository: ProfileRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    val bookingState = bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)
    val driverState = profileRepository.userState.asLiveData(viewModelScope.coroutineContext)
    val customerState = profileRepository.otherUserState.asLiveData(viewModelScope.coroutineContext)

    val currentLocationState = homeRepository.currentLocation.asLiveData(viewModelScope.coroutineContext)

    fun reverseCurrentLocation() = viewModelScope.launch {
        val currentLocation = homeRepository.locationResult.value.value
        if (currentLocation != null) {
            homeRepository.reverseCurrentLocation(currentLocation)
        }
    }
    fun getCustomer(customerId: String) = viewModelScope.launch {
        profileRepository.getCustomer(customerId)
    }
}