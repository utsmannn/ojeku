package com.ojeku.profile.repository

import android.location.Location
import com.ojeku.profile.ProfileMapper
import com.ojeku.profile.services.ProfileWebServices
import com.ojeku.profile.entity.UpdateFcmRequest
import com.ojeku.profile.entity.User
import com.utsman.core.RepositoryProvider
import com.utsman.core.state.StateEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    val userState: StateFlow<StateEvent<User>>
    val otherUserState: StateFlow<StateEvent<User>>
    val fcmUpdateState: StateFlow<StateEvent<Boolean>>

    suspend fun getUser()
    suspend fun updateFcmToken(fcmToken: String)
    suspend fun updateDriverActive(isActive: Boolean)
    suspend fun updateUserLocation(location: Location)

    suspend fun getCustomer(customerId: String)
    suspend fun getDriver(driverId: String)

    private class Impl(private val webServices: ProfileWebServices) : ProfileRepository,
        RepositoryProvider() {

        private val _userState: MutableStateFlow<StateEvent<User>> =
            MutableStateFlow(StateEvent.Idle())
        override val userState: StateFlow<StateEvent<User>>
            get() = _userState

        private val _otherUserState: MutableStateFlow<StateEvent<User>> =
            MutableStateFlow(StateEvent.Idle())
        override val otherUserState: StateFlow<StateEvent<User>>
            get() = _otherUserState

        private val _updateFcmToken: MutableStateFlow<StateEvent<Boolean>> =
            MutableStateFlow(StateEvent.Idle())
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

        override suspend fun updateDriverActive(isActive: Boolean) {
            bindToState(
                stateFlow = _userState,
                onFetch = {
                    webServices.updateDriverActive(isActive)
                },
                mapper = {
                    ProfileMapper.mapResponseToUser(it)
                }
            )
        }

        override suspend fun updateUserLocation(location: Location) {
            webServices.updateUserLocation("${location.latitude},${location.longitude}")
        }

        override suspend fun getCustomer(customerId: String) {
            bindToState(
                stateFlow = _otherUserState,
                onFetch = {
                    webServices.getCustomerById(customerId)
                },
                mapper = {
                    ProfileMapper.mapResponseToUser(it)
                }
            )
        }

        override suspend fun getDriver(driverId: String) {
            bindToState(
                stateFlow = _otherUserState,
                onFetch = {
                    webServices.getDriverById(driverId)
                },
                mapper = {
                    ProfileMapper.mapResponseToUser(it)
                }
            )
        }
    }

    companion object {
        fun build(webServices: ProfileWebServices): ProfileRepository {
            return Impl(webServices)
        }
    }
}