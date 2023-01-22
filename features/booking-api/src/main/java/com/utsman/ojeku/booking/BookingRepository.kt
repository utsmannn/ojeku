package com.utsman.ojeku.booking

import com.utsman.core.RepositoryProvider
import com.utsman.core.extensions.value
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.entity.LocationData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BookingRepository {
    val bookingCustomer: StateFlow<StateEvent<Booking>>

    suspend fun createBookingCustomer(
        fromLocationData: LocationData,
        destinationLocationData: LocationData
    )

    suspend fun getCurrentBooking(
        status: Booking.BookingStatus
    )

    suspend fun requestBookingCustomer(bookingId: String, transType: Booking.TransType)
    suspend fun cancelBookingCustomer(bookingId: String)

    suspend fun restartStateBookingCustomer()

    private class Impl(private val webServices: BookingWebServices) : BookingRepository,
        RepositoryProvider() {

        private val _bookingCustomer: MutableStateFlow<StateEvent<Booking>> =
            MutableStateFlow(StateEvent.Idle())
        override val bookingCustomer: StateFlow<StateEvent<Booking>>
            get() = _bookingCustomer

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