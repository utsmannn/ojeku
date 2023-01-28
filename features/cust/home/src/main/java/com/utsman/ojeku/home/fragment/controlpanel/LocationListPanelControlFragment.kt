package com.utsman.ojeku.home.fragment.controlpanel

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.utsman.core.LocationManager
import com.utsman.core.extensions.onEmpty
import com.utsman.core.extensions.onFailure
import com.utsman.core.extensions.onSuccess
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.LocationApiLayout
import com.utsman.locationapi.databinding.ItemSearchLocationBinding
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.ui.onBindAdapter
import com.utsman.ojeku.home.databinding.FragmentPanelControlLocationListBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.genericAdapter
import com.utsman.utils.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationListPanelControlFragment :
    BindingFragment<FragmentPanelControlLocationListBinding>() {

    private val viewModel: LocationListPanelControlViewModel by viewModel()

    private val locationAdapter by genericAdapter<LocationData>(
        layoutRes = LocationApiLayout.item_search_location,
        onBindItem = { position, item ->
            bindAdapter(this, position, item)
        }
    )

    override fun inflateBinding(): FragmentPanelControlLocationListBinding {
        return FragmentPanelControlLocationListBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.getSavedLocation()
        viewModel.locationListState.observe(this) {

            binding.tvLocationTitle.isVisible = it is StateEvent.Success

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

        binding.rvLocation.layoutManager = LinearLayoutManager(context)
        binding.rvLocation.adapter = locationAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setLocationDest(LocationData())
    }

    private fun bindAdapter(view: View, position: Int, item: LocationData) {
        LocationManager.instance.getLastLocation { location ->
            ItemSearchLocationBinding.bind(view).onBindAdapter(
                position = position,
                item = item,
                currentLocation = location,
                itemCount = locationAdapter.itemCount,
                onClick = { _, _ ->
                    viewModel.setLocationDest(item)
                },
                onToggleClick = {

                }
            )
        }
    }
}