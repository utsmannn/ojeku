package com.utsman.ojeku.home.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.utsman.core.extensions.onLoading
import com.utsman.core.extensions.onSuccess
import com.utsman.ojeku.booking.History
import com.utsman.ojeku.booking.rp
import com.utsman.ojeku.home.R
import com.utsman.ojeku.home.databinding.FragmentHistoryListBinding
import com.utsman.ojeku.home.databinding.ItemHistoryBinding
import com.utsman.ojeku.home.viewmodel.HistoryViewModel
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.genericAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryListFragment : BindingFragment<FragmentHistoryListBinding>() {

    private val viewModel: HistoryViewModel by viewModel()

    private val listener: HistoryFragmentListener? by lazy {
        val parentFragment = childFragmentManager.findFragmentByTag("history")
        parentFragment as? HistoryFragmentListener
    }

    private val historyAdapter by genericAdapter<History>(
        layoutRes = R.layout.item_history,
        onBindItem = { _, item ->
            ItemHistoryBinding.bind(this).bindAdapter(item)
        }
    )

    override fun inflateBinding(): FragmentHistoryListBinding {
        return FragmentHistoryListBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        binding.rvHistory.adapter = historyAdapter

        viewModel.getHistory()
        viewModel.historyState.observe(this) {
            it.onLoading {
                historyAdapter.pushLoading()
            }
            it.onSuccess {
                historyAdapter.pushItems(this)
            }
        }
    }

    private fun ItemHistoryBinding.bindAdapter(item: History) {
        tvOrderId.text = item.id
        tvType.text = when (item.type) {
            "BIKE" -> "TransBike"
            "CAR" -> "TransCar"
            else -> "Undefine"
        }
        tvPrice.text = item.price.rp()
    }
}