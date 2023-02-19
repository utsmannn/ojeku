package com.utsman.driver.home.panelcontrol

import android.os.Bundle
import com.utsman.core.extensions.onSuccess
import com.utsman.driver.home.databinding.FragmentPanelControlOngoingBinding
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.rp
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class OngoingPanelControlFragment : BindingFragment<FragmentPanelControlOngoingBinding>() {
    private val viewModel: OngoingPanelControlViewModel by viewModel()

    override fun inflateBinding(): FragmentPanelControlOngoingBinding {
        return FragmentPanelControlOngoingBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.bookingState.observe(this) {
            it.onSuccess {
                renderControlPanel(this)

                binding.btnFinish.setOnClickListener {
                    viewModel.completeBooking(this.id)
                }
            }
        }
    }

    private fun renderControlPanel(booking: Booking) {
        binding.tvPrice.text = booking.price.rp()
        val textFrom = booking.routeLocation.from.name
        val textDest = booking.routeLocation.destination.name
        val distance = booking.routeLocation.routes.distance.toFloat()
        val distanceIsMeters = when {
            distance < 1000.0 -> "~ ${distance.roundToInt()} M"
            else -> String.format("~ %.2f KM", distance / 1000.0)
        }

        binding.tvCustAddress.text = textFrom
        binding.tvFrom.text = textFrom
        binding.tvDest.text = textDest
        binding.tvDistance.text = distanceIsMeters

        viewModel.getCustomer(booking.customerId)
        viewModel.customerState.observe(this) {
            it.onSuccess {
                binding.tvCustName.text = username
            }
        }
    }
}