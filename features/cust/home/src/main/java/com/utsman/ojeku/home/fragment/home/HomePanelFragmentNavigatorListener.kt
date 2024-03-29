package com.utsman.ojeku.home.fragment.home

import com.utsman.utils.listener.FragmentListener

interface HomePanelFragmentNavigatorListener : FragmentListener {

    fun navigateToLoading()
    fun navigateToLocationListFragment()
    fun navigateToBookingReadyFragment()
    fun navigateToBookingFragment()
    fun navigateToPickupOngoingFragment()
    fun navigateToCancelFragment()
    fun navigateToDone()
}