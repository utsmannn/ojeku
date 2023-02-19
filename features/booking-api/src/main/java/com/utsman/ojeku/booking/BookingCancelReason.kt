package com.utsman.ojeku.booking

import com.utsman.core.data.EquatableProvider

data class BookingCancelReason(
    val id: String,
    val name: String
) : EquatableProvider(id)