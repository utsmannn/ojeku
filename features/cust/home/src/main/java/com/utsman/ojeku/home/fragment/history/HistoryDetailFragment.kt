package com.utsman.ojeku.home.fragment.history

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.fromJson
import com.utsman.ojeku.booking.History
import com.utsman.ojeku.booking.rp
import com.utsman.ojeku.home.databinding.FragmentHistoryDetailBinding
import com.utsman.ojeku.home.viewmodel.HistoryDetailViewModel
import com.utsman.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryDetailFragment : BindingFragment<FragmentHistoryDetailBinding>() {

    private val viewModel: HistoryDetailViewModel by viewModel()

    private val history: History? by lazy {
        val historyJson = arguments?.getString("history")
        historyJson?.fromJson()
    }

    override fun inflateBinding(): FragmentHistoryDetailBinding {
        return FragmentHistoryDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        history?.also { h ->
            binding.tvDate.text = h.time
            binding.tvOrderId.text = h.id
            binding.tvPrice.text = h.price.rp()
            binding.tvType.text = when (h.type) {
                "BIKE" -> "TransBike"
                "CAR" -> "TransCar"
                else -> "Undefine"
            }
        }

        binding.btnBack.setOnClickListener {
            viewModel.back()
        }
    }
}