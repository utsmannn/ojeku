package com.utsman.ojeku.home.fragment.controlpanel.done

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

    val driverState = profileRepository.otherUserState.asLiveData(viewModelScope.coroutineContext)

    fun getDriver(driverId: String) = viewModelScope.launch {
        profileRepository.getDriver(driverId)
    }

    fun done() = viewModelScope.launch {
        bookingRepository.doneState(false)
    }
}