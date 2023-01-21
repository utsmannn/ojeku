package com.utsman.core

import com.utsman.core.local.AppPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object CoreModules {

    fun modules() = module {
        single { LocationManager(androidContext()) }
        single { AppPreferences(get()) }
        single { CoroutineBus() }
    }
}