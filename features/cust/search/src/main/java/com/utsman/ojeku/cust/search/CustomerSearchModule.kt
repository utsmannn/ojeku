package com.utsman.ojeku.cust.search

import com.utsman.locationapi.LocationWebServices
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object CustomerSearchModule {

    fun modules() = module {
        single { LocationWebServices.build() }
        factory { SearchLocationRepository.build(get(), get()) }
        viewModel { SearchLocationViewModel(get()) }
    }
}