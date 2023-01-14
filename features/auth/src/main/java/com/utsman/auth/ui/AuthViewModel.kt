package com.utsman.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.auth.AuthRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val authSafeScope = viewModelScope + CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    }

    val signInState = authRepository.loginState.asLiveData(authSafeScope.coroutineContext)
    val signUpState = authRepository.registerState.asLiveData(authSafeScope.coroutineContext)

    fun signIn(username: String, password: String) = authSafeScope.launch {
        authRepository.postLogin(username, password)
    }

    fun signUp(username: String, password: String) = authSafeScope.launch {
        authRepository.postRegister(username, password)
    }

    fun saveToken(token: String) = authSafeScope.launch {
        authRepository.saveToken(token)
    }
}