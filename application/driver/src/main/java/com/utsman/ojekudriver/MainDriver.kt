package com.utsman.ojekudriver

import android.app.Application
import com.utsman.core.KoinStarter

class MainDriver : Application() {

    override fun onCreate() {
        super.onCreate()
        KoinStarter.onCreate(this)
    }
}