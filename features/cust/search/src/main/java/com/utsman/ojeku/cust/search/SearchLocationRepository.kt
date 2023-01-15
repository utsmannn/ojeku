package com.utsman.ojeku.cust.search

import com.utsman.core.RepositoryProvider
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.LocationWebServices
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface SearchLocationRepository {
    val locationState: StateFlow<StateEvent<List<LocationData>>>

    suspend fun searchLocation(name: String, coordinate: String)

    private class Impl(private val webServices: LocationWebServices) : SearchLocationRepository, RepositoryProvider() {

        private val _locationState: MutableStateFlow<StateEvent<List<LocationData>>> =
            MutableStateFlow(StateEvent.Idle())
        override val locationState: StateFlow<StateEvent<List<LocationData>>>
            get() = _locationState

        private var currentQuery = ""

        override suspend fun searchLocation(name: String, coordinate: String) {
            if (currentQuery != name) {
                currentQuery = name

                bindToState(
                    stateFlow = _locationState,
                    onFetch = {
                        webServices.searchLocation(name, coordinate)
                    },
                    mapper = {
                        Mapper.mapLocationResponseToData(it)
                    }
                )
            }
        }
    }

    companion object {
        fun build(webServices: LocationWebServices): SearchLocationRepository {
            return Impl(webServices)
        }
    }
}