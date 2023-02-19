package com.utsman.ojeku.home.uistate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface HistoryUiStateManager {

    val uiState: LiveData<HistoryUiState>

    suspend fun changeState(uiState: HistoryUiState)

    private class Impl : HistoryUiStateManager {

        private val _uiState: MutableLiveData<HistoryUiState> = MutableLiveData(HistoryUiState.List)
        override val uiState: LiveData<HistoryUiState>
            get() = _uiState

        override suspend fun changeState(uiState: HistoryUiState) {
            _uiState.value = uiState
        }
    }

    companion object {
        fun build(): HistoryUiStateManager {
            return Impl()
        }
    }
}