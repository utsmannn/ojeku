package com.utsman.ojeku

import com.utsman.utils.listener.FragmentListener

interface HomeFragmentListener : FragmentListener {
    fun onMessageFromActivity(message: String)
}