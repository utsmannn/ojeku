package com.utsman.ojeku.home.di

import com.utsman.ojeku.home.fragment.controlpanel.LocationListPanelControlViewModel
import com.utsman.ojeku.home.repo.HomeRepository
import com.utsman.ojeku.home.repo.HomeRepositoryImpl
import com.utsman.ojeku.home.repo.LocationListRepository
import com.utsman.ojeku.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

object HomeModule {
    fun module() = org.koin.dsl.module {
        factory<HomeRepository> { HomeRepositoryImpl(get(), get()) }
        factory { LocationListRepository.build(get()) }

        viewModel { HomeViewModel(get()) }
        viewModel { LocationListPanelControlViewModel(get(), get()) }
    }
}