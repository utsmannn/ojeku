package com.utsman.ojeku.booking

import com.utsman.core.RepositoryProvider
import com.utsman.core.extensions.value
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.entity.LocationData
import com.utsman.network.ServiceMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BookingRepository {
    val bookingCustomer: StateFlow<StateEvent<Booking>>
    val rejectBookingState: StateFlow<StateEvent<Boolean>>
    val acceptBookingState: StateFlow<StateEvent<Booking>>

    suspend fun createBookingCustomer(
        fromLocationData: LocationData,
        destinationLocationData: LocationData
    )

    suspend fun getCurrentBooking(
        status: Booking.BookingStatus
    )

    suspend fun getBookingById(bookingId: String)

    suspend fun requestBookingCustomer(bookingId: String, transType: Booking.TransType)
    suspend fun cancelBookingCustomer(bookingId: String)
    suspend fun cancelByService(serviceMessage: ServiceMessage)

    suspend fun rejectBookingDriver(bookingId: String)
    suspend fun acceptBookingDriver(bookingId: String)

    suspend fun restartStateBookingCustomer()

    private class Impl(private val webServices: BookingWebServices) : BookingRepository,
        RepositoryProvider() {

        private val _bookingCustomer: MutableStateFlow<StateEvent<Booking>> =
            MutableStateFlow(StateEvent.Idle())
        override val bookingCustomer: StateFlow<StateEvent<Booking>>
            get() = _bookingCustomer

        private val _rejectBookingState: MutableStateFlow<StateEvent<Boolean>> =
            MutableStateFlow(StateEvent.Idle())
        override val rejectBookingState: StateFlow<StateEvent<Boolean>>
            get() = _rejectBookingState

        private val _acceptBookingState: MutableStateFlow<StateEvent<Booking>> =
            MutableStateFlow(StateEvent.Idle())
        override val acceptBookingState: StateFlow<StateEvent<Booking>>
            get() = _acceptBookingState

        override suspend fun createBookingCustomer(
            fromLocationData: LocationData,
            destinationLocationData: LocationData
        ) {
            val currentBooking = bookingCustomer.value.value
            if (currentBooking?.status == Booking.BookingStatus.READY) {
                cancelBookingCustomer(currentBooking.id)
            }


            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    val from =
                        "${fromLocationData.latLng.latitude},${fromLocationData.latLng.longitude}"
                    val dest =
                        "${destinationLocationData.latLng.latitude},${destinationLocationData.latLng.longitude}"
                    webServices.postBookingCustomer(from, dest)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun getCurrentBooking(status: Booking.BookingStatus) {
            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.getCurrentBookingCustomer(status)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun getBookingById(bookingId: String) {
            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.getBookingById(bookingId)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun requestBookingCustomer(
            bookingId: String,
            transType: Booking.TransType
        ) {
            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.requestBookingCustomer(bookingId, transType)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun cancelBookingCustomer(bookingId: String) {
            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.cancelBookingCustomer(bookingId)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun cancelByService(serviceMessage: ServiceMessage) {
            _bookingCustomer.value = StateEvent.Failure(Throwable(serviceMessage.message))
        }

        override suspend fun rejectBookingDriver(bookingId: String) {
            bindToState(
                stateFlow = _rejectBookingState,
                onFetch = {
                    webServices.rejectBookingDriver(bookingId)
                },
                mapper = {
                    it.data ?: false
                }
            )
        }

        override suspend fun acceptBookingDriver(bookingId: String) {
            bindToState(
                stateFlow = _acceptBookingState,
                onFetch = {
                    webServices.acceptBookingDriver(bookingId)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun restartStateBookingCustomer() {
            _bookingCustomer.value = StateEvent.Idle()
        }
    }

    companion object {
        fun build(webServices: BookingWebServices): BookingRepository {
            return Impl(webServices)
        }
    }
}