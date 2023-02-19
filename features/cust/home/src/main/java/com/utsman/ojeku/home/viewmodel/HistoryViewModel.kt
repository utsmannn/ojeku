package com.utsman.ojeku.home.viewmodel

import androidx.lifecycle.ViewModel
import com.utsman.ojeku.home.uistate.HistoryUiStateManager

class HistoryViewModel(
    private val uiStateManager: HistoryUiStateManager
) : ViewModel() {

    val uiState = uiStateManager.uiState
}