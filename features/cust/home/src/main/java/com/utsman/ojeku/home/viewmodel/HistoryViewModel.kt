package com.utsman.ojeku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val historyState = bookingRepository.historyState.asLiveData(viewModelScope.coroutineContext)

    fun getHistory() = viewModelScope.launch {
        bookingRepository.getHistory()
    }
}