package com.utsman.ojeku.home.fragment.controlpanel

import android.os.Bundle
import com.utsman.core.extensions.onSuccess
import com.utsman.core.view.component.TransportCardView
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.rp
import com.utsman.ojeku.home.databinding.FragmentPanelControlReadyBinding
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReadyPanelControlFragment : BindingFragment<FragmentPanelControlReadyBinding>() {

    private val viewModel: ReadyPanelControlViewModel by viewModel()

    override fun inflateBinding(): FragmentPanelControlReadyBinding {
        return FragmentPanelControlReadyBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.transportView.currentPosition = 0
        binding.transportView.onSelected = { transportCardView ->
            binding.tvTextTransport.text = transportCardView.name
            binding.tvTextTransportDescription.text = transportCardView.description

            viewModel.transType = when (transportCardView.type) {
                TransportCardView.Type.BIKE -> Booking.TransType.BIKE
                TransportCardView.Type.CAR, TransportCardView.Type.TAXI -> Booking.TransType.CAR
            }

        }

        viewModel.bookingState.observe(this) {
            it.onSuccess {
                binding.tvTextPrice.text = price.rp()

                binding.btnBook.setOnClickListener {
                    viewModel.requestBooking(this.id)
                }
            }
        }
    }
}