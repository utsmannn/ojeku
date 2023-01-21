package com.utsman.ojeku.cust.search

import com.utsman.core.RepositoryProvider
import com.utsman.core.extensions.IOScope
import com.utsman.core.extensions.value
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.LocationWebServices
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.local.SavedLocationDao
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

interface SearchLocationRepository {
    val locationState: StateFlow<StateEvent<List<LocationData>>>

    suspend fun searchLocation(name: String, coordinate: String)
    suspend fun toggleSaveLocation(locationData: LocationData)
    suspend fun isSavedLocation(locationData: LocationData): Boolean

    private class Impl(
        private val webServices: LocationWebServices,
        private val savedLocationDao: SavedLocationDao
    ) : SearchLocationRepository, RepositoryProvider() {

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
                            .run {
                                invalidateLocationState(this)
                            }
                    }
                )
            }
        }

        override suspend fun toggleSaveLocation(locationData: LocationData) {
            IOScope().launch {
                if (isSavedLocation(locationData)) {
                    savedLocationDao.deleteLocation(locationData.latLng.toString())
                } else {
                    savedLocationDao.insertNewSavedLocation(Mapper.mapDataToEntity(locationData))
                }
                val newLocationData = invalidateLocationState(locationState.value.value.orEmpty())
                _locationState.value = StateEvent.Success(newLocationData)
            }
        }

        override suspend fun isSavedLocation(locationData: LocationData): Boolean {
            return savedLocationDao.isExists(locationData.latLng.toString())
        }

        private fun invalidateLocationState(oldLocationData: List<LocationData>): List<LocationData> {
            IOScope().launch {
                println("OJEKU .... -> ${savedLocationDao.getSavedLocation().first().map { it.latLng }}")
            }
            return oldLocationData.map { locationData ->
                locationData.apply {
                    isSaved = runBlocking { isSavedLocation(locationData) }
                }
            }
        }
    }

    companion object {
        fun build(
            webServices: LocationWebServices,
            savedLocationDao: SavedLocationDao
        ): SearchLocationRepository {
            return Impl(webServices, savedLocationDao)
        }
    }
}