package com.utsman.ojeku.home.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ojeku.profile.entity.User
import com.utsman.core.LocationManager
import com.utsman.core.extensions.onEmpty
import com.utsman.core.extensions.onFailure
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.toJson
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.LocationApiLayout
import com.utsman.locationapi.databinding.ItemSearchLocationBinding
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.ui.onBindAdapter
import com.utsman.navigation.intentTo
import com.utsman.ojeku.home.activity.EditLocationActivity
import com.utsman.ojeku.home.databinding.FragmentProfileBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.genericAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BindingFragment<FragmentProfileBinding>() {
    private val viewModel: ProfileViewModel by viewModel()

    private val locationAdapter by genericAdapter<LocationData>(
        layoutRes = LocationApiLayout.item_search_location,
        onBindItem = { position, item ->
            bindAdapter(this, position, item)
        }
    )

    override fun inflateBinding(): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.rvSavedLocation.layoutManager = LinearLayoutManager(context)
        binding.rvSavedLocation.adapter = locationAdapter

        viewModel.getUser()
        viewModel.getSavedLocation()

        viewModel.userState.observe(this) {
            it.onSuccess {
                bindToUser(this)
            }
        }

        viewModel.locationListState.observe(this) {
            it.onSuccess {
                locationAdapter.changeItem(this)
            }
            it.onFailure {
                locationAdapter.pushError(this)
            }
            it.onEmpty {
                locationAdapter.pushEmpty()
            }
        }
    }

    private fun bindToUser(user: User) {
        binding.etUsername.setText(user.username)
        binding.etUsername.hint = user.username
    }

    private fun bindAdapter(view: View, position: Int, item: LocationData) {
        LocationManager.instance.getLastLocation { location ->
            ItemSearchLocationBinding.bind(view).onBindAdapter(
                position = position,
                item = item,
                currentLocation = location,
                itemCount = locationAdapter.itemCount,
                onClick = { _, _ ->
                    context?.intentTo(EditLocationActivity::class) {
                        it.putExtra("saved_location", item.toJson())
                    }
                },
                onToggleClick = {

                },
                onBind = {
                    it.imgBookmark.isVisible = false
                }
            )
        }
    }
}