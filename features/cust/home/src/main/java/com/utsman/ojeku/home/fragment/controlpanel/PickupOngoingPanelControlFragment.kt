package com.utsman.ojeku.home.fragment.controlpanel

import android.os.Bundle
import com.ojeku.profile.entity.mapToDriverExtra
import com.utsman.core.extensions.onSuccess
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.rp
import com.utsman.ojeku.home.databinding.FragmentPanelControlPickupOngoingBinding
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickupOngoingPanelControlFragment : BindingFragment<FragmentPanelControlPickupOngoingBinding>() {

    private val viewModel: PickupOngoingPanelControlViewModel by viewModel()

    override fun inflateBinding(): FragmentPanelControlPickupOngoingBinding {
        return FragmentPanelControlPickupOngoingBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.bookingState.observe(this) {
            it.onSuccess {
                viewModel.getDriver(driverId)

                val transTypeName = when (transType) {
                    Booking.TransType.CAR -> "TransCar"
                    Booking.TransType.BIKE -> "TransBike"
                }

                val priceRp = this.price.rp()

                binding.tvTransType.text = transTypeName
                binding.tvPrice.text = priceRp

                when (this.status) {
                    Booking.BookingStatus.ACCEPTED -> {
                        binding.tvDriverStatus.text = "Driver is on the way"
                        binding.btnCancel.text = "Cancel Booking"

                        binding.btnCancel.setOnClickListener {
                            viewModel.cancel(id)
                        }
                    }
                    Booking.BookingStatus.ONGOING -> {
                        binding.tvDriverStatus.text = "Driver is taking customer"
                        binding.btnCancel.text = "Report problem"

                        binding.btnCancel.setOnClickListener {
                            viewModel.cancel(id)
                        }
                    }
                    else -> {}
                }
            }
        }

        viewModel.estimatedDuration.observe(this) {
            binding.tvEstimatedTime.text = it
        }

        viewModel.driverState.observe(this) {
            it.onSuccess {
                binding.tvDriverName.text = username
                binding.tvDriverPlat.text = userExtra.mapToDriverExtra().vehiclesNumber
            }
        }
    }
}