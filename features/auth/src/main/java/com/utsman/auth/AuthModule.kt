package com.utsman.auth

import com.utsman.auth.ui.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AuthModule {

    fun modules() = module {
        single { AuthWebServicesProvider.providedWebServices() }
        factory { AuthRepository.build(get(), get()) }

        viewModel { AuthViewModel(get()) }
    }
}