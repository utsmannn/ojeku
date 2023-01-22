package com.utsman.driver.home

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object HomeModule {

    fun modules() = module {
        single { HomeRepository.build(get()) }
        viewModel { HomeViewModel(get(), get()) }
    }
}