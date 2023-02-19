package com.utsman.ojeku.booking

import com.utsman.core.data.EquatableProvider

data class History(
    val id: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val time: String
): EquatableProvider(id)