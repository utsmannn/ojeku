package com.utsman.ojeku.home.fragment.controlpanel.done

import android.os.Bundle
import com.ojeku.profile.entity.mapToDriverExtra
import com.utsman.core.extensions.onSuccess
import com.utsman.ojeku.booking.rp
import com.utsman.ojeku.home.databinding.FragmentPanelControlDoneBinding
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class DonePanelControlFragment : BindingFragment<FragmentPanelControlDoneBinding>() {
    private val viewModel: DonePanelControlViewModel by viewModel()

    override fun inflateBinding(): FragmentPanelControlDoneBinding {
        return FragmentPanelControlDoneBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.currentBooking.observe(this) {
            it.onSuccess {
                binding.tvDate.text = time
                binding.tvOrderId.text = id

                binding.tvPrice.text = price.rp()
                viewModel.getDriver(driverId)
            }
        }

        viewModel.driverState.observe(this) {
            it.onSuccess {
                binding.tvDriverName.text = username
                binding.tvDriverPlat.text = userExtra.mapToDriverExtra().vehiclesNumber
            }
        }

        binding.btnDone.setOnClickListener {
            viewModel.done()
        }
    }
}