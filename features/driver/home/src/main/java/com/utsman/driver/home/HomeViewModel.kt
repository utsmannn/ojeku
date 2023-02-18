package com.utsman.driver.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val locationResult = homeRepository.locationResult.asLiveData(viewModelScope.coroutineContext)
    val currentUser = profileRepository.userState.asLiveData(viewModelScope.coroutineContext)
    val customerUser = profileRepository.otherUserState.asLiveData(viewModelScope.coroutineContext)
    val currentBooking =
        bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)
    val rejectBooking =
        bookingRepository.rejectBookingState.asLiveData(viewModelScope.coroutineContext)

    val doneUiState = bookingRepository.doneUiState.asLiveData(viewModelScope.coroutineContext)

    var isDisableFragmentLoading = false
    var currentStatusBooking = Booking.BookingStatus.UNDEFINE
    var currentBookingId = ""

    var savedMargin: PanelMargin = PanelMargin()

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

    fun getCustomer(customerId: String) = viewModelScope.launch {
        profileRepository.getCustomer(customerId)
    }

    fun getBooking(bookingId: String) = viewModelScope.launch {
        bookingRepository.restartStateBookingCustomer()
        bookingRepository.getBookingById(bookingId)
    }

    fun rejectBooking(bookingId: String) = viewModelScope.launch {
        bookingRepository.rejectBookingDriver(bookingId)
    }

    fun acceptBooking(bookingId: String) = viewModelScope.launch {
        bookingRepository.acceptBookingDriver(bookingId)
    }

    fun updatePickupRoute(routes: Booking.Routes) = viewModelScope.launch {
        bookingRepository.updatePickupRoute(routes)
    }

    fun clearPickupRoute() = viewModelScope.launch {
        bookingRepository.clearPickupRoute()
    }

    fun showDonePanel(isShow: Boolean) = viewModelScope.launch {
        bookingRepository.doneState(isShow)
    }
}