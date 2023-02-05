package com.utsman.ojeku.home.fragment.controlpanel.ready

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class ReadyPanelControlViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val bookingState = bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)

    var transType = Booking.TransType.BIKE

    fun requestBooking(bookingId: String) = viewModelScope.launch {
        bookingRepository.requestBookingCustomer(bookingId, transType)
    }
}