package com.ojeku.profile.di

import com.ojeku.profile.repository.ProfileRepository
import com.ojeku.profile.services.ProfileWebServices
import org.koin.dsl.module

object ProfileModule {
    fun modules() = module {
        single { ProfileWebServices.build() }
        factory { ProfileRepository.build(get()) }
    }
}