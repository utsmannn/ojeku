package com.utsman.auth

import com.utsman.auth.request.SignRequest
import com.utsman.core.RepositoryProvider
import com.utsman.core.state.StateEvent
import com.utsman.network.Tokenizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val loginState: StateFlow<StateEvent<String>>
    val registerState: StateFlow<StateEvent<Boolean>>

    suspend fun postLogin(username: String, password: String)
    suspend fun postRegister(
        username: String,
        password: String,
        vehiclesNumber: String,
        type: String
    )

    suspend fun saveToken(token: String)

    private class Impl(
        private val authWebServices: AuthWebServices,
        private val tokenizer: Tokenizer
    ) : AuthRepository, RepositoryProvider() {

        private val _loginState: MutableStateFlow<StateEvent<String>> =
            MutableStateFlow(StateEvent.Idle())
        override val loginState: StateFlow<StateEvent<String>>
            get() = _loginState

        private val _registerState: MutableStateFlow<StateEvent<Boolean>> =
            MutableStateFlow(StateEvent.Idle())
        override val registerState: StateFlow<StateEvent<Boolean>>
            get() = _registerState

        override suspend fun postLogin(username: String, password: String) {
            bindToState(
                stateFlow = _loginState,
                onFetch = {
                    val request = SignRequest(username, password)
                    authWebServices.login(request)
                },
                mapper = {
                    it.data?.token.orEmpty()
                }
            )
        }

        override suspend fun postRegister(
            username: String,
            password: String,
            vehiclesNumber: String,
            type: String
        ) {
            bindToState(
                stateFlow = _registerState,
                onFetch = {
                    if (type == "customer") {
                        val request = SignRequest(username, password)
                        authWebServices.registerCustomer(request)
                    } else {
                        val request = SignRequest(username, password, vehiclesNumber)
                        authWebServices.registerDriver(request)
                    }
                },
                mapper = {
                    it.data ?: false
                }
            )
        }

        override suspend fun saveToken(token: String) {
            tokenizer.token = token
        }
    }

    companion object {
        fun build(webServices: AuthWebServices, tokenizer: Tokenizer): AuthRepository {
            return Impl(webServices, tokenizer)
        }
    }
}