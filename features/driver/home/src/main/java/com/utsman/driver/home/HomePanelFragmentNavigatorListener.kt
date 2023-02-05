package com.utsman.driver.home

interface HomePanelFragmentNavigatorListener {

    fun hidePanel()
    fun navigateToLoading()
    fun navigateToPickup()
    fun navigateToOngoing()
    fun navigateToComplete()
}