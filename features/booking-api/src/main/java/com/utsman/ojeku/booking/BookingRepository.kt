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
    val historyState: StateFlow<StateEvent<List<History>>>

    val estimatedDuration: StateFlow<String>
    val pickupRoute: StateFlow<StateEvent<Booking.Routes>>

    val cancelUiState: StateFlow<Boolean>
    val doneUiState: StateFlow<Boolean>

    suspend fun createBookingCustomer(
        fromLocationData: LocationData,
        destinationLocationData: LocationData
    )

    suspend fun getCurrentBooking(
        status: Booking.BookingStatus
    )

    suspend fun getBookingById(bookingId: String)

    suspend fun requestBookingCustomer(bookingId: String, transType: Booking.TransType)
    suspend fun cancelBookingCustomer(bookingId: String, reasonId: String)
    suspend fun cancelByService(serviceMessage: ServiceMessage)

    suspend fun rejectBookingDriver(bookingId: String)
    suspend fun acceptBookingDriver(bookingId: String)
    suspend fun takeBookingDriver(bookingId: String)
    suspend fun completeBookingDriver(bookingId: String)
    suspend fun getHistory()

    suspend fun restartStateBookingCustomer()

    suspend fun updateEstimatedDuration(updated: String)
    suspend fun updatePickupRoute(routes: Booking.Routes)
    suspend fun clearPickupRoute()

    suspend fun getReason()

    fun cancelState(showCancelPanel: Boolean)
    fun doneState(showDonePanel: Boolean)

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

        private val _historyState: MutableStateFlow<StateEvent<List<History>>> =
            MutableStateFlow(StateEvent.Idle())
        override val historyState: StateFlow<StateEvent<List<History>>>
            get() = _historyState

        private val _estimatedDuration = MutableStateFlow("")
        override val estimatedDuration: StateFlow<String>
            get() = _estimatedDuration

        private val _pickupRoute: MutableStateFlow<StateEvent<Booking.Routes>> =
                MutableStateFlow(StateEvent.Idle())
        override val pickupRoute: StateFlow<StateEvent<Booking.Routes>>
            get() = _pickupRoute

        private val _cancelUiState: MutableStateFlow<Boolean> = MutableStateFlow(false)
        override val cancelUiState: StateFlow<Boolean>
            get() = _cancelUiState

        private val _doneUiState: MutableStateFlow<Boolean> = MutableStateFlow(false)
        override val doneUiState: StateFlow<Boolean>
            get() = _doneUiState

        override suspend fun createBookingCustomer(
            fromLocationData: LocationData,
            destinationLocationData: LocationData
        ) {
            val currentBooking = bookingCustomer.value.value
            if (currentBooking?.status == Booking.BookingStatus.READY) {
                cancelBookingCustomer(currentBooking.id, "OTHER")
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

        override suspend fun cancelBookingCustomer(bookingId: String, reasonId: String) {
            bindToState(
                stateFlow = _bookingCustomer,
                onFetch = {
                    webServices.cancelBookingCustomer(bookingId, reasonId)
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

        override suspend fun getHistory() {
            bindToState(
                stateFlow = _historyState,
                onFetch = {
                    webServices.getActivity()
                },
                mapper = {
                    BookingMapper.mapHistoryToData(it)
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

        override suspend fun getReason() {
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

        override fun cancelState(showCancelPanel: Boolean) {
            _cancelUiState.value = showCancelPanel
        }

        override fun doneState(showDonePanel: Boolean) {
            _doneUiState.value = showDonePanel
        }
    }

    companion object {
        fun build(webServices: BookingWebServices): BookingRepository {
            return Impl(webServices)
        }
    }
}