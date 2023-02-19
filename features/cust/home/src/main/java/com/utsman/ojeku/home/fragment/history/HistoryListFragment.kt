package com.utsman.ojeku.home.fragment.history

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.onIdle
import com.utsman.core.extensions.onLoading
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.toast
import com.utsman.ojeku.booking.History
import com.utsman.ojeku.booking.rp
import com.utsman.ojeku.home.R
import com.utsman.ojeku.home.databinding.FragmentHistoryListBinding
import com.utsman.ojeku.home.databinding.ItemHistoryBinding
import com.utsman.ojeku.home.viewmodel.HistoryListViewModel
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.genericAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryListFragment : BindingFragment<FragmentHistoryListBinding>() {

    private val viewModel: HistoryListViewModel by viewModel()

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

        viewModel.historyState.observe(this) {
            it.onIdle {
                viewModel.getHistory()
            }
            it.onLoading {
                historyAdapter.pushLoading()
            }
            it.onSuccess {
                historyAdapter.pushItems(this)
            }
        }

        CoroutineBus.getInstance().getLiveData<Any>("update_history", lifecycleScope)
            .observe(this) {
                lifecycleScope.launch {
                    historyAdapter.clearItems()
                    viewModel.getHistory()
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
        root.setOnClickListener {
            viewModel.goToDetail(item)
        }
    }
}