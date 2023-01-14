package com.utsman.profile

import com.utsman.core.RepositoryProvider
import com.utsman.core.state.StateEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val userState: StateFlow<StateEvent<User>>

    suspend fun getUser()

    private class Impl(private val webServices: ProfileWebServices) : ProfileRepository, RepositoryProvider() {

        private val _userState: MutableStateFlow<StateEvent<User>> = MutableStateFlow(StateEvent.Idle())
        override val userState: StateFlow<StateEvent<User>>
            get() = _userState

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
    }

    companion object {
        fun build(webServices: ProfileWebServices) : ProfileRepository {
            return Impl(webServices)
        }
    }
}