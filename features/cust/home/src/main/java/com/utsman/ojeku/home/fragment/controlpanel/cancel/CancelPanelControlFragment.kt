package com.utsman.ojeku.home.fragment.controlpanel.cancel

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.flexbox.FlexboxLayoutManager
import com.utsman.core.CoreColor
import com.utsman.core.extensions.onSuccess
import com.utsman.ojeku.booking.BookingCancelReason
import com.utsman.ojeku.home.R
import com.utsman.ojeku.home.databinding.FragmentPanelControlCancelBinding
import com.utsman.ojeku.home.databinding.ItemReasonCancelBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.GenericAdapter
import com.utsman.utils.adapter.genericAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class CancelPanelControlFragment : BindingFragment<FragmentPanelControlCancelBinding>() {

    private val viewModel: CancelPanelControlViewModel by viewModel()

    private val cancelReasonAdapter: GenericAdapter<BookingCancelReason> by genericAdapter(
        layoutRes = R.layout.item_reason_cancel,
        onBindItem = { position, item ->
            ItemReasonCancelBinding.bind(this).bindAdapter(position, item)
        }
    )

    override fun inflateBinding(): FragmentPanelControlCancelBinding {
        return FragmentPanelControlCancelBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.btnCancelPanel.setOnClickListener {
            viewModel.cancelPanel()
        }

        viewModel.getReason()
        viewModel.reasonState.observe(this) {
            it.onSuccess {
                cancelReasonAdapter.pushItems(this)
            }
        }

        val layoutManager = FlexboxLayoutManager(context)
        binding.rvReasonCancel.layoutManager = layoutManager
        binding.rvReasonCancel.adapter = cancelReasonAdapter

        viewModel.currentReason.observe(this) { currentReason ->
            binding.btnCancel.isEnabled = currentReason != null
            cancelReasonAdapter.notifyDataSetChanged()

            currentReason?.let {
                binding.btnCancel.setOnClickListener {
                    viewModel.cancel(currentReason.id)
                }
            }
        }
    }

    private fun ItemReasonCancelBinding.bindAdapter(position: Int, item: BookingCancelReason) {
        itemTvReason.text = item.name

        val currentReason = viewModel.currentReason.value

        val (backgroundRes, textColor) = if (currentReason == item) {
            Pair(R.drawable.bg_reason_dark, CoreColor.white)
        } else {
            Pair(R.drawable.bg_reason_gray, CoreColor.gray)
        }

        itemTvReason.setBackgroundResource(backgroundRes)
        itemTvReason.setTextColor(ContextCompat.getColor(root.context, textColor))

        itemTvReason.setOnClickListener {
            viewModel.updateCurrentReason(item)
        }
    }
}