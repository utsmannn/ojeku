package com.utsman.ojeku

import android.location.Location
import com.utsman.core.state.StateEvent
import com.utsman.core.state.StateEventManager

interface HomeRepository {
    val locationResult: StateEventManager<Location>

    suspend fun getLocation()
}