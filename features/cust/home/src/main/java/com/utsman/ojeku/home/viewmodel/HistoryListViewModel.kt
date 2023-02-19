package com.utsman.ojeku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.core.extensions.toJson
import com.utsman.ojeku.booking.BookingRepository
import com.utsman.ojeku.booking.History
import com.utsman.ojeku.home.uistate.HistoryUiState
import com.utsman.ojeku.home.uistate.HistoryUiStateManager
import kotlinx.coroutines.launch

class HistoryListViewModel(
    private val bookingRepository: BookingRepository,
    private val historyUiStateManager: HistoryUiStateManager
) : ViewModel() {

    val historyState = bookingRepository.historyState.asLiveData(viewModelScope.coroutineContext)

    fun getHistory() = viewModelScope.launch {
        bookingRepository.getHistory()
    }

    fun goToDetail(history: History) = viewModelScope.launch {
        val uiState = HistoryUiState.Detail(history)
        historyUiStateManager.changeState(uiState)
        println("Asuuuuuuu -> ${history.toJson()}")
    }
}