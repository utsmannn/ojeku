package com.utsman.ojeku.home.fragment.history

import com.utsman.ojeku.booking.History

interface HistoryFragmentListener {
    fun onBack()
    fun navigateToDetail(history: History)
}