package com.utsman.ojeku.home.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.utsman.core.extensions.convertEventToSubscriber
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.value
import com.utsman.core.state.StateEventSubscriber
import com.utsman.locationapi.entity.LocationData
import com.utsman.network.ServiceMessage
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.BookingRepository
import com.utsman.ojeku.home.repo.HomeRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class HomeViewModel(
    private val repository: HomeRepository,
    private val bookingRepository: BookingRepository
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

    val bookingState = bookingRepository.bookingCustomer.asLiveData(viewModelScope.coroutineContext)

    val filledLocationState: LiveData<Triple<Boolean, LocationData, LocationData>>
        get() {
            return repository.locationFrom.combine(repository.locationDestination) { from, dest ->
                val fromValue = from.value
                val destValue = dest.value
                val isFromValid = fromValue != null && fromValue.latLng != LatLng(0.0, 0.0)
                val isDestValid = destValue != null && destValue.latLng != LatLng(0.0, 0.0)
                val isFilled = isFromValid && isDestValid

                Triple(isFilled, fromValue ?: LocationData(), destValue ?: LocationData())
            }.asLiveData(viewModelScope.coroutineContext)
        }

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

    fun createBooking(fromLocationData: LocationData, destinationLocationData: LocationData) =
        viewModelScope.launch {
            bookingRepository.createBookingCustomer(fromLocationData, destinationLocationData)
        }

    fun getBooking(bookingId: String) = viewModelScope.launch {
        bookingRepository.getBookingById(bookingId)
    }

    fun cancelCurrentReadyBooking() = bookingState.value?.onSuccess {
        viewModelScope.launch {
            if (status == Booking.BookingStatus.READY) {
                bookingRepository.cancelBookingCustomer(id)
            }
        }
    }

    fun cancelCurrentReadyBookingByServices(message: ServiceMessage) = viewModelScope.launch {
        if (message.type == ServiceMessage.Type.BOOKING_UNAVAILABLE) {
            bookingRepository.cancelByService(message)
        }
    }

    fun getCurrentReadyBooking() = viewModelScope.launch {
        bookingRepository.getCurrentBooking(Booking.BookingStatus.READY)
    }

    fun retryBooking() = viewModelScope.launch {
        val currentBooking = bookingState.value?.value
        if (currentBooking != null && currentBooking.status == Booking.BookingStatus.REQUEST_RETRY) {
            bookingRepository.requestBookingCustomer(currentBooking.id, currentBooking.transType)
        }
    }

    fun setClearThrowableHandler() {
        _throwable = MutableStateFlow(null)
    }
}