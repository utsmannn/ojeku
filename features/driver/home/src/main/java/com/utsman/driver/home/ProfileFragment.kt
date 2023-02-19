package com.utsman.driver.home

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ojeku.profile.entity.User
import com.utsman.core.extensions.onLoading
import com.utsman.core.extensions.onSuccess
import com.utsman.driver.home.databinding.FragmentProfileBinding
import com.utsman.driver.home.databinding.ItemHistoryBinding
import com.utsman.ojeku.booking.History
import com.utsman.ojeku.booking.rp
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.genericAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BindingFragment<FragmentProfileBinding>() {

    private val viewModel: ProfileViewModel by viewModel()

    private val historyAdapter by genericAdapter<History>(
        layoutRes = R.layout.item_history,
        onBindItem = { _, item ->
            ItemHistoryBinding.bind(this).bindAdapter(item)
        }
    )

    override fun inflateBinding(): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.getUser()
        viewModel.getHistory()

        viewModel.userState.observe(this) {
            it.onSuccess {
                bindToUser(this)
            }
        }

        viewModel.historyState.observe(this) {
            it.onLoading {
                historyAdapter.pushLoading()
            }
            it.onSuccess {
                historyAdapter.pushItems(this)
            }
        }

        binding.rvRideHistory.layoutManager = LinearLayoutManager(context)
        binding.rvRideHistory.adapter = historyAdapter
    }

    private fun bindToUser(user: User) {
        binding.etUsername.setText(user.username)
        binding.etUsername.hint = user.username
    }

    private fun ItemHistoryBinding.bindAdapter(item: History) {
        tvOrderId.text = item.id
        tvType.text = when (item.type) {
            "BIKE" -> "TransBike"
            "CAR" -> "TransCar"
            else -> "Undefine"
        }
        tvPrice.text = item.price.rp()
        root.setOnClickListener {
            //
        }
    }
}