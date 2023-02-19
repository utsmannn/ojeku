package com.utsman.ojeku.home.fragment.controlpanel.cancel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.core.extensions.value
import com.utsman.ojeku.booking.BookingCancelReason
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class CancelPanelControlViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val reasonState = bookingRepository.cancelReasonState.asLiveData(viewModelScope.coroutineContext)

    private val _currentReason: MutableLiveData<BookingCancelReason?> = MutableLiveData(null)
    val currentReason: LiveData<BookingCancelReason?>
        get() = _currentReason

    fun cancel(reasonId: String) = viewModelScope.launch {
        val currentBooking = bookingRepository.bookingCustomer.value.value
        val bookingId = currentBooking?.id
        bookingId?.let { bookingRepository.cancelBookingCustomer(it, reasonId) }
    }

    fun cancelPanel() = viewModelScope.launch {
        bookingRepository.cancelState(false)
    }

    fun getReason() = viewModelScope.launch {
        bookingRepository.getReason()
    }

    fun updateCurrentReason(reason: BookingCancelReason) = viewModelScope.launch {
        _currentReason.value = reason
    }
}