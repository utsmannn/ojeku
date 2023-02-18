package com.utsman.driver.home.panelcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class DonePanelControlViewModel(
    private val bookingRepository: BookingRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val currentBooking =
        bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)

    val customerState = profileRepository.otherUserState.asLiveData(viewModelScope.coroutineContext)

    fun getCustomer(driverId: String) = viewModelScope.launch {
        profileRepository.getCustomer(driverId)
    }

    fun done() = viewModelScope.launch {
        bookingRepository.doneState(false)
    }
}