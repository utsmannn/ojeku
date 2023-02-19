package com.utsman.ojeku.home.uistate

import com.utsman.ojeku.booking.History

sealed class HistoryUiState {

    object List : HistoryUiState()
    data class Detail(val history: History) : HistoryUiState()
}