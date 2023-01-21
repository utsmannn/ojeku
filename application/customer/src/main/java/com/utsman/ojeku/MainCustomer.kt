package com.utsman.ojeku

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.utsman.koin.KoinStarter
import com.utsman.locationapi.LocationApiModule
import com.utsman.ojeku.booking.BookingModule
import com.utsman.ojeku.home.di.HomeModule

class MainCustomer : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinStarter.onCreate(this, listOf(
            HomeModule.module(),
            MainModule.modules(),
            BookingModule.modules()
        ))

        MainUtils.getFcmToken { token ->
            println("ASUUUUUU => $token")
        }
    }
}