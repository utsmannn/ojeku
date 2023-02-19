package com.utsman.ojeku.home.fragment.history

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.utsman.core.extensions.toJson
import com.utsman.navigation.attachFragment
import com.utsman.navigation.replaceFragment
import com.utsman.ojeku.booking.History
import com.utsman.ojeku.home.databinding.FragmentHistoryBinding
import com.utsman.ojeku.home.uistate.HistoryUiState
import com.utsman.ojeku.home.viewmodel.HistoryViewModel
import com.utsman.utils.BindingFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : BindingFragment<FragmentHistoryBinding>() {

    private val viewModel: HistoryViewModel by viewModel()

    override fun inflateBinding(): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.uiState.observe(this) { uiState ->
            when (uiState) {
                is HistoryUiState.List -> navigateToList()
                is HistoryUiState.Detail -> navigateToDetail(uiState.history)
            }
        }
    }

    private fun navigateToList() {
        childFragmentManager.replaceFragment(binding.historyFrame, HistoryListFragment::class)
    }

    private fun navigateToDetail(history: History) {
        childFragmentManager.replaceFragment(
            binding.historyFrame,
            HistoryDetailFragment::class,
            bundle = bundleOf("history" to history.toJson())
        )
    }
}