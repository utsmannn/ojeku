package com.utsman.driver.home

import com.utsman.driver.home.panelcontrol.DonePanelControlViewModel
import com.utsman.driver.home.panelcontrol.PickupPanelControlViewModel
import com.utsman.driver.home.panelcontrol.OngoingPanelControlViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object HomeModule {

    fun modules() = module {
        single { HomeRepository.build(get(), get()) }

        viewModel { HomeViewModel(get(), get(), get()) }
        viewModel { PickupPanelControlViewModel(get(), get(), get()) }
        viewModel { OngoingPanelControlViewModel(get(), get()) }
        viewModel { DonePanelControlViewModel(get(), get()) }
        viewModel { ProfileViewModel(get(), get()) }
    }
}