package com.utsman.navigation

import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

interface ProfileFragmentConnector {
    val profileFragment: KClass<Fragment>
}