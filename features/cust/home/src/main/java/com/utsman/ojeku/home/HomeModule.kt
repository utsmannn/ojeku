package com.utsman.ojeku.home

import org.koin.androidx.viewmodel.dsl.viewModel

object HomeModule {
    fun module() = org.koin.dsl.module {
        single<HomeRepository> { HomeRepositoryImpl(get(), get()) }

        viewModel { HomeViewModel(get()) }
    }
}