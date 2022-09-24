package com.utsman.profile

import androidx.fragment.app.Fragment
import com.utsman.navigation.ProfileFragmentConnector
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ProfileFragmentConnectorProvider : ProfileFragmentConnector {
    override val profileFragment: KClass<Fragment>
        get() = ProfileFragment::class as KClass<Fragment>
}