package com.utsman.ojeku

import android.location.Location
import com.utsman.utils.listener.ActivityListener

interface MainActivityListener : ActivityListener {
    fun onLocationResult(data: Location)
}