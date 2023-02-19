package com.utsman.ojeku.booking

import org.koin.dsl.module

object BookingModule {

    fun modules() = module {
        single { BookingWebServices.build() }
        single { BookingRepository.build(get()) }
    }
}