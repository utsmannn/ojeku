package com.utsman.driver.home.panelcontrol

import android.os.Bundle
import com.utsman.core.extensions.onSuccess
import com.utsman.driver.home.databinding.FragmentPanelControlPickupBinding
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class PickupPanelControlFragment : BindingFragment<FragmentPanelControlPickupBinding>() {

    private val viewModel: PickupPanelControlViewModel by viewModel()

    override fun inflateBinding(): FragmentPanelControlPickupBinding {
        return FragmentPanelControlPickupBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.reverseCurrentLocation()
        viewModel.currentLocationState.observe(this) {
            it.onSuccess {
                binding.tvDriverAddress.text = name
            }
        }

        viewModel.bookingState.observe(this) {
            it.onSuccess {
                val pickupAddress = routeLocation.from.name
                binding.tvCustAddress.text = pickupAddress

                viewModel.getCustomer(customerId)
                val distance = routeLocation.routes.distance.toFloat()
                val distanceIsMeters = when {
                    distance < 1000.0 -> "~ ${distance.roundToInt()} M"
                    else -> String.format("~ %.2f KM", distance / 1000.0)
                }
                binding.tvDistance.text = distanceIsMeters
            }
        }

        viewModel.customerState.observe(this) {
            it.onSuccess {
                binding.tvCustName.text = username
            }
        }
    }
}