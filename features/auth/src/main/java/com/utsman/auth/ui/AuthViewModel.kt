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

    private val authSafeScope =
        viewModelScope + CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }

    val signInState = authRepository.loginState.asLiveData(authSafeScope.coroutineContext)
    val signUpState = authRepository.registerState.asLiveData(authSafeScope.coroutineContext)

    fun signIn(username: String, password: String) = authSafeScope.launch {
        authRepository.postLogin(username, password)
    }

    fun signUpCustomer(username: String, password: String) = authSafeScope.launch {
        authRepository.postRegister(username, password, "", "customer")
    }

    fun signUpDriver(username: String, password: String, vehiclesNumber: String) =
        authSafeScope.launch {
            authRepository.postRegister(username, password, vehiclesNumber, "driver")
        }

    fun saveToken(token: String) = authSafeScope.launch {
        authRepository.saveToken(token)
    }
}