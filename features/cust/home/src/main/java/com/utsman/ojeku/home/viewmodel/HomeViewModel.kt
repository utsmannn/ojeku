package com.utsman.ojeku.home.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.utsman.core.extensions.convertEventToSubscriber
import com.utsman.core.state.StateEventSubscriber
import com.utsman.locationapi.entity.LocationData
import com.utsman.ojeku.home.repo.HomeRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {
    private val locationEvent = repository.locationResult
    private val locationScope = locationEvent.createScope(viewModelScope)
    private var _throwable = MutableStateFlow<Throwable?>(null)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _throwable.value = throwable
    }

    private val safeScope = viewModelScope + exceptionHandler

    val initialLocation = repository.initialLocation.asLiveData(safeScope.coroutineContext)
    val locationFrom = repository.locationFrom.asLiveData(safeScope.coroutineContext)
    val locationDestination = repository.locationDestination.asLiveData(safeScope.coroutineContext)
    val throwableHandler = _throwable.asLiveData(safeScope.coroutineContext)

    fun subscribeLocation(subscriber: StateEventSubscriber<Location>) {
        convertEventToSubscriber(locationEvent, subscriber)
    }

    fun getInitialLocation(location: Location) {
        safeScope.launch {
            repository.reverseCurrentLocation(location)
        }
    }

    fun setLocationFrom(locationData: LocationData) = viewModelScope.launch {
        repository.setFromLocation(locationData)
    }

    fun setLocationDest(locationData: LocationData) = viewModelScope.launch {
        repository.setDestinationLocation(locationData)
    }

    fun getLocation() = locationScope.launch {
        repository.getLocation()
    }

    fun setClearThrowableHandler() {
        _throwable = MutableStateFlow(null)
    }
}