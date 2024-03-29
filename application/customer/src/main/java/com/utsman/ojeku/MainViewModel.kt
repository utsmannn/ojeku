package com.utsman.ojeku

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.core.state.ContentUiState
import com.utsman.ojeku.home.uistate.HistoryUiState
import com.utsman.ojeku.home.uistate.HistoryUiStateManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MainViewModel(
    private val profileRepository: ProfileRepository,
    private val historyUiState: HistoryUiStateManager
) : ViewModel() {

    private val profileSafeScope =
        viewModelScope + CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }

    val userState = profileRepository.userState.asLiveData(profileSafeScope.coroutineContext)
    val fcmUpdateState =
        profileRepository.fcmUpdateState.asLiveData(profileSafeScope.coroutineContext)

    private val _contentUiState: MutableLiveData<ContentUiState> = MutableLiveData()
    val contentUiState: LiveData<ContentUiState> get() = _contentUiState

    fun getCurrentUser() = profileSafeScope.launch {
        profileRepository.getUser()
    }

    fun updateFcmToken(fcmToken: String) = profileSafeScope.launch {
        profileRepository.updateFcmToken(fcmToken)
    }

    fun postStateSplash() {
        _contentUiState.value = ContentUiState.Splash
    }

    fun postStateContent() {
        _contentUiState.value = ContentUiState.Content
    }

    fun isHistoryDetail(): Boolean {
        return historyUiState.uiState.value is HistoryUiState.Detail
    }

    fun backFromHistoryDetail() = viewModelScope.launch {
        historyUiState.changeState(HistoryUiState.List)
    }

}