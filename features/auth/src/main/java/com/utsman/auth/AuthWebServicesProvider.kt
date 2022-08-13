package com.utsman.auth

object AuthWebServicesProvider {

    fun providedWebServices(): AuthWebServices {
        return AuthWebServices.build()
    }
}