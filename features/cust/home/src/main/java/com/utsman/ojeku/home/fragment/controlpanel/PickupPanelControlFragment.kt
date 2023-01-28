package com.utsman.ojeku.home.fragment.controlpanel

import android.os.Bundle
import com.ojeku.profile.entity.mapToDriverExtra
import com.utsman.core.extensions.onSuccess
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.home.databinding.FragmentPanelControlPickupBinding
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickupPanelControlFragment : BindingFragment<FragmentPanelControlPickupBinding>() {

    private val viewModel: PickupPanelControlViewModel by viewModel()
    override fun inflateBinding(): FragmentPanelControlPickupBinding {
        return FragmentPanelControlPickupBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {

        viewModel.bookingState.observe(this) {
            it.onSuccess {
                viewModel.getDriver(driverId)

                val transTypeName = when (transType) {
                    Booking.TransType.CAR -> "TransCar"
                    Booking.TransType.BIKE -> "TransBike"
                }
                binding.tvTransType.text = transTypeName

                binding.btnCancel.setOnClickListener {
                    viewModel.cancel(id)
                }
            }
        }

        viewModel.driverState.observe(this) {
            it.onSuccess {
                binding.tvDriverName.text = username
                binding.tvDriverPlat.text = userExtra.mapToDriverExtra().vehiclesNumber
            }
        }
    }
}