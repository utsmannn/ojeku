package com.utsman.driver.home.panelcontrol

import android.os.Bundle
import com.utsman.core.extensions.onSuccess
import com.utsman.driver.home.databinding.FragmentPanelControlDoneBinding
import com.utsman.ojeku.booking.rp
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class DonePanelControlFragment : BindingFragment<FragmentPanelControlDoneBinding>() {
    private val viewModel: DonePanelControlViewModel by viewModel()

    override fun inflateBinding(): FragmentPanelControlDoneBinding {
        return FragmentPanelControlDoneBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.btnDone.setOnClickListener {
            viewModel.done()
        }

        viewModel.currentBooking.observe(this) {
            it.onSuccess {
                binding.tvDate.text = time
                binding.tvOrderId.text = id

                binding.tvPrice.text = price.rp()
                viewModel.getCustomer(customerId)
            }
        }

        viewModel.customerState.observe(this) {
            it.onSuccess {
                binding.tvCustomerName.text = username
            }
        }
    }
}