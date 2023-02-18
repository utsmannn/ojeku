package com.utsman.ojeku.home.fragment

import com.utsman.ojeku.booking.History

interface HistoryFragmentListener {
    fun onBack()
    fun navigateToDetail(history: History)
}