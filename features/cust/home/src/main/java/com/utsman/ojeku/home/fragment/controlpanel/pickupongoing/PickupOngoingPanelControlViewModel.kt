package com.utsman.ojeku.home.fragment.controlpanel.pickupongoing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class PickupOngoingPanelControlViewModel(
    private val bookingRepository: BookingRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val bookingState = bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)
    val driverState = profileRepository.otherUserState.asLiveData(viewModelScope.coroutineContext)
    val estimatedDuration = bookingRepository.estimatedDuration.asLiveData(viewModelScope.coroutineContext)

    fun cancel() = viewModelScope.launch {
        bookingRepository.cancelState(true)
    }

    fun getDriver(driverId: String) = viewModelScope.launch {
        profileRepository.getDriver(driverId)
    }
}