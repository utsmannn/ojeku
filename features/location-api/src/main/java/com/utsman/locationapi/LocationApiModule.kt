package com.utsman.locationapi

import com.utsman.locationapi.local.SavedLocationDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

object LocationApiModule {

    fun modules() = module {
        single { SavedLocationDatabase.build(androidApplication()) }
        single { SavedLocationDatabase.instances().savedLocationDao() }
        single { SavedLocationDatabase.instances().recentLocationDao() }
    }
}