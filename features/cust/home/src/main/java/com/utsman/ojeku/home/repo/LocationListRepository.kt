package com.utsman.ojeku.home.repo

import com.utsman.core.LocationManager
import com.utsman.core.RepositoryProvider
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface LocationListRepository {
    val locationListState: StateFlow<StateEvent<List<LocationData>>>

    fun getSavedLocationList()
    fun getRecentLocationList()

    private class Impl() : LocationListRepository, RepositoryProvider() {

        private val _locationListState: MutableStateFlow<StateEvent<List<LocationData>>> =
            MutableStateFlow(StateEvent.Idle())

        override val locationListState: StateFlow<StateEvent<List<LocationData>>>
            get() = _locationListState

        override fun getSavedLocationList() {
            _locationListState.value = StateEvent.Empty()
        }

        override fun getRecentLocationList() {
            _locationListState.value = StateEvent.Empty()
        }
    }

    companion object {
        fun build(): LocationListRepository {
            return Impl()
        }
    }
}