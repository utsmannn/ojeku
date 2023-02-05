package com.utsman.ojeku.socket

import org.koin.dsl.module

object SocketModule {

    fun modules() = module {
        single { SocketWrapper() }
    }
}