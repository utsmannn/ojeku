package com.utsman.ojeku.home.activity

import com.utsman.core.extensions.IOScope
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.Mapper
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.local.SavedLocationDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface EditLocationRepository {
    val editLocationState: StateFlow<StateEvent<Boolean>>

    suspend fun editLocation(locationData: LocationData)

    private class Impl(private val locationDao: SavedLocationDao) : EditLocationRepository {

        private val _editLocationState: MutableStateFlow<StateEvent<Boolean>> =
            MutableStateFlow(StateEvent.Idle())
        override val editLocationState: StateFlow<StateEvent<Boolean>>
            get() = _editLocationState

        override suspend fun editLocation(locationData: LocationData) {
            IOScope().launch {
                _editLocationState.value = StateEvent.Loading()
                val entity = Mapper.mapDataToEntity(locationData)
                locationDao.updateLocation(entity)

                _editLocationState.value = StateEvent.Success(true)
            }
        }
    }

    companion object {
        fun build(locationDao: SavedLocationDao): EditLocationRepository {
            return Impl(locationDao)
        }
    }
}