package com.utsman.core

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object CoreModules {

    fun modules() = module {
        single { LocationManager(androidContext()) }
    }
}