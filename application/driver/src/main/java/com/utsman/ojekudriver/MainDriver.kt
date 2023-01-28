package com.utsman.ojekudriver

import android.app.Application
import com.ojeku.profile.di.ProfileModule
import com.utsman.driver.home.HomeModule
import com.utsman.koin.KoinStarter
import com.utsman.ojeku.booking.BookingModule

class MainDriver : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinStarter.onCreate(this, listOf(
            MainModule.modules(),
            HomeModule.modules(),
            BookingModule.modules(),
            ProfileModule.modules()
        ))
    }
}