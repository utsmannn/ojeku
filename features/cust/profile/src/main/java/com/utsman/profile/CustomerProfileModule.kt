package com.utsman.profile

import com.utsman.navigation.ProfileFragmentConnector

object CustomerProfileModule {
    fun module() = org.koin.dsl.module {
        single<ProfileFragmentConnector> { ProfileFragmentConnectorProvider() }
    }
}