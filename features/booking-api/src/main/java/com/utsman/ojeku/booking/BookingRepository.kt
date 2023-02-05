package com.utsman.ojeku.booking

import com.utsman.core.RepositoryProvider
import com.utsman.core.extensions.IOScope
import com.utsman.core.extensions.value
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.entity.LocationData
import com.utsman.network.ServiceMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

interface BookingRepository {
    val bookingCustomer: StateFlow<StateEvent<Booking>>
    val rejectBookingState: StateFlow<StateEvent<Boolean>>
    val cancelReasonState: StateFlow<StateEvent<List<BookingCancelReason>>>

    val estimatedDuration: StateFlow<String>
    val pickupRoute: StateFlow<StateEvent<Booking.Routes>>

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
    suspend fun takeBookingDriver(bookingId: String)
    suspend fun completeBookingDriver(bookingId: String)

    suspend fun restartStateBookingCustomer()

    suspend fun updateEstimatedDuration(updated: String)
    suspend fun updatePickupRoute(routes: Booking.Routes)
    suspend fun clearPickupRoute()

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

        private val _cancelReasonState: MutableStateFlow<StateEvent<List<BookingCancelReason>>> =
            MutableStateFlow(StateEvent.Idle())
        override val cancelReasonState: StateFlow<StateEvent<List<BookingCancelReason>>>
            get() = _cancelReasonState

        private val _estimatedDuration = MutableStateFlow("")
        override val estimatedDuration: StateFlow<String>
            get() = _estimatedDuration

        private val _pickupRoute: MutableStateFlow<StateEvent<Booking.Routes>> =
                MutableStateFlow(StateEvent.Idle())
        override val pickupRoute: StateFlow<StateEvent<Booking.Routes>>
            get() = _pickupRoute

        init {
            IOScope().launch {
                bindToState(
                    stateFlow = _cancelReasonState,
                    onFetch = {
                        webServices.reasonBookingCustomer()
                    },
                    mapper = {
                        it.data.orEmpty().filterNotNull()
                            .map { response ->
                                BookingCancelReason(
                                    id = response.id.orEmpty(),
                                    name = response.name.orEmpty()
                                )
                            }
                    }
                )
            }
        }

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
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.acceptBookingDriver(bookingId)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun takeBookingDriver(bookingId: String) {
            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.takeBookingDriver(bookingId)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun completeBookingDriver(bookingId: String) {
            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.completeBookingDriver(bookingId)
                },
                mapper = {
                    BookingMapper.mapResponseToBooking(it)
                }
            )
        }

        override suspend fun restartStateBookingCustomer() {
            _bookingCustomer.value = StateEvent.Idle()
        }

        override suspend fun updateEstimatedDuration(updated: String) {
            _estimatedDuration.value = updated
        }

        override suspend fun updatePickupRoute(routes: Booking.Routes) {
            _pickupRoute.value = StateEvent.Success(routes)
        }

        override suspend fun clearPickupRoute() {
            _pickupRoute.value = StateEvent.Idle()
        }
    }

    companion object {
        fun build(webServices: BookingWebServices): BookingRepository {
            return Impl(webServices)
        }
    }
}