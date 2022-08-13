package com.utsman.auth

import org.koin.dsl.module

object AuthModule {

    fun modules() = module {
        single { AuthWebServicesProvider.providedWebServices() }
    }
}