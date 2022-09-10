package com.utsman.locationapi

import com.utsman.locationapi.ui.SearchLocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object LocationApiModule {

    fun modules() = module {
        single { LocationWebServices.build() }
        viewModel { SearchLocationViewModel(get()) }
    }
}