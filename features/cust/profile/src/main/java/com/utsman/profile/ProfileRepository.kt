package com.utsman.profile

import com.utsman.core.RepositoryProvider
import com.utsman.core.state.StateEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val userState: StateFlow<StateEvent<User>>
    val fcmUpdateState: StateFlow<StateEvent<Boolean>>

    suspend fun getUser()
    suspend fun updateFcmToken(fcmToken: String)

    private class Impl(private val webServices: ProfileWebServices) : ProfileRepository, RepositoryProvider() {

        private val _userState: MutableStateFlow<StateEvent<User>> = MutableStateFlow(StateEvent.Idle())
        override val userState: StateFlow<StateEvent<User>>
            get() = _userState

        private val _updateFcmToken: MutableStateFlow<StateEvent<Boolean>> = MutableStateFlow(StateEvent.Idle())
        override val fcmUpdateState: StateFlow<StateEvent<Boolean>>
            get() = _updateFcmToken

        override suspend fun getUser() {
            bindToState(
                stateFlow = _userState,
                onFetch = {
                    webServices.getUser()
                },
                mapper = {
                    ProfileMapper.mapResponseToUser(it)
                }
            )
        }

        override suspend fun updateFcmToken(fcmToken: String) {
            bindToState(
                stateFlow = _updateFcmToken,
                onFetch = {
                    webServices.updateFcmToken(UpdateFcmRequest(fcmToken))
                },
                mapper = {
                    it.status ?: false
                }
            )
        }
    }

    companion object {
        fun build(webServices: ProfileWebServices) : ProfileRepository {
            return Impl(webServices)
        }
    }
}