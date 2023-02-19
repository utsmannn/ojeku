package com.utsman.driver.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ojeku.profile.repository.ProfileRepository
import com.utsman.ojeku.booking.BookingRepository
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    val historyState = bookingRepository.historyState.asLiveData(viewModelScope.coroutineContext)
    val userState = profileRepository.userState.asLiveData(viewModelScope.coroutineContext)

    fun getHistory() = viewModelScope.launch {
        bookingRepository.getHistory()
    }

    fun getUser() = viewModelScope.launch {
        profileRepository.getUser()
    }
}