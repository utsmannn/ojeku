package com.utsman.ojeku.home.fragment

import android.os.Bundle
import com.utsman.core.extensions.toJson
import com.utsman.ojeku.booking.History
import com.utsman.ojeku.home.databinding.FragmentHistoryBinding
import com.utsman.utils.BindingFragment

class HistoryFragment : BindingFragment<FragmentHistoryBinding>(), HistoryFragmentListener {
    override fun inflateBinding(): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        childFragmentManager.beginTransaction()
            .replace(binding.historyFrame.id, HistoryListFragment())
    }

    override fun onBack() {
        childFragmentManager.popBackStack()
    }

    override fun navigateToDetail(history: History) {
        val historyDetailFragment = HistoryDetailFragment()
        historyDetailFragment.arguments?.putString("history", history.toJson())

        childFragmentManager.beginTransaction()
            .replace(binding.historyFrame.id, historyDetailFragment)
    }
}