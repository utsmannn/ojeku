package com.utsman.driver.home.panelcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class OngoingPanelControlViewModel(
    private val bookingRepository: BookingRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val bookingState = bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)
    val customerState = profileRepository.otherUserState.asLiveData(viewModelScope.coroutineContext)

    fun getCustomer(customerId: String) = viewModelScope.launch {
        profileRepository.getCustomer(customerId)
    }

    fun completeBooking(bookingId: String) = viewModelScope.launch {
        bookingRepository.completeBookingDriver(bookingId)
    }
}