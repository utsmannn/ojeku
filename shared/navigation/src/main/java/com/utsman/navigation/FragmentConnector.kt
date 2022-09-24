package com.utsman.navigation

import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object FragmentConnector : KoinComponent {

    val Profile: ProfileFragmentConnector by inject()
}