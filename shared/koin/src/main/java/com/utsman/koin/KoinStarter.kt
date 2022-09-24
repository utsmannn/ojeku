package com.utsman.koin

import android.content.Context
import com.utsman.auth.AuthModule
import com.utsman.core.CoreModules
import com.utsman.network.NetworkModule
import com.utsman.ojeku.cust.search.CustomerSearchModule
import com.utsman.profile.CustomerProfileModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

object KoinStarter {

    fun onCreate(context: Context, featureModule: List<Module> = emptyList()) {
        val modules = listOf(
            CoreModules.modules(),
            NetworkModule.modules(),
            AuthModule.modules(),
            CustomerSearchModule.modules(),
            CustomerProfileModule.module()
        ) + featureModule
        startKoin {
            androidContext(context)
            modules(modules)
        }
    }
}