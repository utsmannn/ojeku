package com.utsman.ojeku

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.profile.ProfileRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MainViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val profileSafeScope = viewModelScope + CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    }

    val userState = profileRepository.userState.asLiveData(profileSafeScope.coroutineContext)
    val fcmUpdateState = profileRepository.fcmUpdateState.asLiveData(profileSafeScope.coroutineContext)

    fun getCurrentUser() = profileSafeScope.launch {
        profileRepository.getUser()
    }

    fun updateFcmToken(fcmToken: String) = profileSafeScope.launch {
        profileRepository.updateFcmToken(fcmToken)
    }

}