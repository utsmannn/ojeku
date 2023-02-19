package com.utsman.ojeku.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utsman.ojeku.home.uistate.HistoryUiState
import com.utsman.ojeku.home.uistate.HistoryUiStateManager
import kotlinx.coroutines.launch

class HistoryDetailViewModel(
    private val historyUiStateManager: HistoryUiStateManager
) : ViewModel() {

    fun back() = viewModelScope.launch {
        historyUiStateManager.changeState(HistoryUiState.List)
    }
}