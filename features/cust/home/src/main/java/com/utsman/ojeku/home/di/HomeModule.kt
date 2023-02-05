package com.utsman.ojeku.home.di

import com.utsman.ojeku.home.fragment.controlpanel.booking.BookingPanelControlViewModel
import com.utsman.ojeku.home.fragment.controlpanel.cancel.CancelPanelControlViewModel
import com.utsman.ojeku.home.fragment.controlpanel.locationlist.LocationListPanelControlViewModel
import com.utsman.ojeku.home.fragment.controlpanel.pickupongoing.PickupOngoingPanelControlViewModel
import com.utsman.ojeku.home.fragment.controlpanel.ready.ReadyPanelControlViewModel
import com.utsman.ojeku.home.repo.HomeRepository
import com.utsman.ojeku.home.repo.HomeRepositoryImpl
import com.utsman.ojeku.home.repo.LocationListRepository
import com.utsman.ojeku.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel

object HomeModule {
    fun module() = org.koin.dsl.module {
        single<HomeRepository> { HomeRepositoryImpl(get(), get()) } // use in home vm and panel vm
        factory { LocationListRepository.build(get()) }

        viewModel { HomeViewModel(get(), get()) }
        viewModel { LocationListPanelControlViewModel(get(), get()) }
        viewModel { ReadyPanelControlViewModel(get()) }
        viewModel { BookingPanelControlViewModel(get()) }
        viewModel { PickupOngoingPanelControlViewModel(get(), get()) }
        viewModel { CancelPanelControlViewModel(get()) }
    }
}