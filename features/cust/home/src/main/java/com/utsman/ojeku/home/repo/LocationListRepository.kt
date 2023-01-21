package com.utsman.ojeku.home.repo

import com.utsman.core.LocationManager
import com.utsman.core.RepositoryProvider
import com.utsman.core.extensions.IOScope
import com.utsman.core.extensions.value
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.local.SavedLocationDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

interface LocationListRepository {
    val locationListState: StateFlow<StateEvent<List<LocationData>>>

    suspend fun getSavedLocationList()
    suspend fun getRecentLocationList()

    suspend fun toggleSaveLocation(locationData: LocationData)
    suspend fun isSavedLocation(locationData: LocationData): Boolean

    private class Impl(
        private val savedLocationDao: SavedLocationDao
    ) : LocationListRepository, RepositoryProvider() {

        private val _locationListState: MutableStateFlow<StateEvent<List<LocationData>>> =
            MutableStateFlow(StateEvent.Idle())

        override val locationListState: StateFlow<StateEvent<List<LocationData>>>
            get() = _locationListState

        override suspend fun getSavedLocationList() {
            val savedLocation = savedLocationDao.getSavedLocation()
            savedLocation.collect { locationEntity ->
                locationEntity.map { Mapper.mapEntityToData(it) }
                    .take(3)
                    .run {
                        val newState = if (isEmpty()) {
                            StateEvent.Empty()
                        } else {
                            StateEvent.Success(invalidateLocationState(this))
                        }
                        _locationListState.value = newState
                    }
            }
        }

        override suspend fun getRecentLocationList() {
            _locationListState.value = StateEvent.Empty()
        }

        override suspend fun toggleSaveLocation(locationData: LocationData) {
            IOScope().launch {
                if (isSavedLocation(locationData)) {
                    savedLocationDao.deleteLocation(locationData.latLng.toString())
                } else {
                    savedLocationDao.insertNewSavedLocation(Mapper.mapDataToEntity(locationData))
                }
                val newLocationData = invalidateLocationState(locationListState.value.value.orEmpty())
                _locationListState.value = StateEvent.Success(newLocationData)
            }
        }

        override suspend fun isSavedLocation(locationData: LocationData): Boolean {
            return savedLocationDao.isExists(locationData.latLng.toString())
        }

        private fun invalidateLocationState(oldLocationData: List<LocationData>): List<LocationData> {
            IOScope().launch {
            }
            return oldLocationData.map { locationData ->
                locationData.apply {
                    isSaved = runBlocking { isSavedLocation(locationData) }
                }
            }
        }
    }

    companion object {
        fun build(savedLocationDao: SavedLocationDao): LocationListRepository {
            return Impl(savedLocationDao)
        }
    }
}