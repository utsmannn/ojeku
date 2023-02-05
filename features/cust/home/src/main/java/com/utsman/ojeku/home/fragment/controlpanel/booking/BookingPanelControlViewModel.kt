package com.utsman.ojeku.home.fragment.controlpanel.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.ojeku.booking.BookingCancelReason
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class BookingPanelControlViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val bookingState = bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)

    fun cancel(bookingId: String) = viewModelScope.launch {
        bookingRepository.cancelBookingCustomer(bookingId, "OTHER")
    }
}