package com.utsman.ojeku

import android.app.Application
import com.utsman.koin.KoinStarter
import com.utsman.ojeku.home.HomeModule

class MainCustomer : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinStarter.onCreate(this, listOf(
            HomeModule.module(),
            MainModule.modules()
        ))
    }
}