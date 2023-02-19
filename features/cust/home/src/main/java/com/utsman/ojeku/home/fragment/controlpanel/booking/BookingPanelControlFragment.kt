package com.utsman.ojeku.home.fragment.controlpanel.booking

import android.os.Bundle
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.onSuccess
import com.utsman.core.state.StateEvent
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.home.databinding.FragmentPanelControlBookingBinding
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class BookingPanelControlFragment : BindingFragment<FragmentPanelControlBookingBinding>() {

    private val viewModel: BookingPanelControlViewModel by viewModel()

    override fun inflateBinding(): FragmentPanelControlBookingBinding {
        return FragmentPanelControlBookingBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.bookingState.observe(this) {
            binding.btnCancel.isEnabled = it is StateEvent.Success

            it.onSuccess {
                onReady(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineBus.getInstance().post("hide_navigation_menu", true)
    }

    override fun onPause() {
        super.onPause()
        CoroutineBus.getInstance().post("hide_navigation_menu", false)
    }

    private fun onReady(booking: Booking) {
        binding.btnCancel.setOnClickListener {
            viewModel.cancel(booking.id)
        }
    }
}