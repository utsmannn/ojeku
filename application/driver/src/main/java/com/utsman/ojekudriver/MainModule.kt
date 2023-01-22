package com.utsman.ojekudriver

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object MainModule {

    fun modules() = module {
        single { ActivityNavigatorProvider.build() }
        viewModel { MainViewModel(get()) }
    }
}