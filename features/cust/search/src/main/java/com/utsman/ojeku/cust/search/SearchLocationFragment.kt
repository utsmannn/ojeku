package com.utsman.ojeku.cust.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.utsman.core.CoroutineBus
import com.utsman.core.LocationManager
import com.utsman.core.extensions.*
import com.utsman.core.state.StateEvent
import com.utsman.core.view.component.InputLocationView
import com.utsman.locationapi.LocationApiLayout
import com.utsman.locationapi.databinding.ItemSearchLocationBinding
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.ui.onBindAdapter
import com.utsman.ojeku.cust.search.databinding.FragmentSearchBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.genericAdapter
import com.utsman.utils.listener.findActivityListener
import com.utsman.utils.snackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SearchLocationFragment : BindingFragment<FragmentSearchBinding>() {

    private val viewModel: SearchLocationViewModel by inject()

    override fun inflateBinding(): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    private val formType: Int by lazy {
        arguments?.getInt("formType", 1) ?: 1
    }

    private val fromLocationExtra by lazy {
        arguments?.getParcelable("location_from") ?: LocationData()
    }

    private val destLocationExtra by lazy {
        arguments?.getParcelable("location_dest") ?: LocationData()
    }

    private val searchAdapter by genericAdapter<LocationData>(
        layoutRes = LocationApiLayout.item_search_location,
        onBindItem = { position, item ->
            bindAdapter(this, position, item)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationManager.instance.getLastLocation {
            viewModel.currentLocation = it
        }
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.inputSearch.setFocus(formType)

        when (formType) {
            1 -> {
                viewModel.setFromLocationData(LocationData())
                viewModel.setDestLocationData(destLocationExtra)
            }
            2 -> {
                viewModel.setFromLocationData(fromLocationExtra)
                viewModel.setDestLocationData(LocationData())
            }
        }

        viewModel.fromLocationData.observe(this) { locationData ->
            if (locationData.latLng.latitude != 0.0) {
                binding.inputSearch.inputLocationFromData = InputLocationView.InputLocationData(
                    location = locationData.latLng.toLocation(),
                    name = locationData.name
                )
            }
            lifecycleScope.launch {
                searchAdapter.clearItems()
                binding.containerLocation.isVisible = false
                hideKeyboard()
                delay(2000)
                viewModel.isEnableFromSearch = true
            }
        }

        viewModel.destLocationData.observe(this) { locationData ->
            if (locationData.latLng.latitude != 0.0) {
                binding.inputSearch.inputLocationDestData = InputLocationView.InputLocationData(
                    location = locationData.latLng.toLocation(),
                    name = locationData.name
                )
            }
            lifecycleScope.launch {
                searchAdapter.clearItems()
                binding.containerLocation.isVisible = false
                hideKeyboard()
                delay(2000)
                viewModel.isEnableDestSearch = true
            }
        }

        viewModel.filledLocationData.observe(this) { (isFilled, from, dest) ->
            val locationData = Pair(from, dest)
            if (isFilled) {
                CoroutineBus.getInstance().post("location_input_filled", locationData)
                activity?.onBackPressed()
            }
        }

        binding.inputSearch.textFlowFrom()
            .asLiveData(lifecycleScope.coroutineContext)
            .observe(this) { text ->
                viewModel.searchType = 1
                if (text.length > 3 && viewModel.isEnableFromSearch) {
                    viewModel.getLocations(text, viewModel.currentLocation)
                } else {
                    searchAdapter.changeItem(emptyList())
                    binding.containerLocation.isVisible = false
                }
            }

        binding.inputSearch.textFlowDest()
            .asLiveData(lifecycleScope.coroutineContext)
            .observe(this) { text ->
                viewModel.searchType = 2
                if (text.length > 3 && viewModel.isEnableDestSearch) {
                    viewModel.getLocations(text, viewModel.currentLocation)
                } else {
                    searchAdapter.changeItem(emptyList())
                    binding.containerLocation.isVisible = false
                }
            }

        binding.rvLocation.layoutManager = LinearLayoutManager(context)
        binding.rvLocation.adapter = searchAdapter

        viewModel.searchLocationState.observe(this) { state ->
            binding.containerLocation.isVisible = state !is StateEvent.Idle

            state.onLoading {
                renderLoading()
            }
            state.onFailure {
                this.printStackTrace()
                renderFailure(this)
            }
            state.onSuccess {
                renderSuccess(this)
            }
        }
    }

    private fun renderLoading() {
        searchAdapter.pushLoading()
    }

    private fun renderSuccess(data: List<LocationData>) {
        searchAdapter.changeItem(data)
    }

    private fun renderFailure(throwable: Throwable) {
        searchAdapter.pushError(throwable)
    }

    private fun bindAdapter(view: View, position: Int, item: LocationData) {
        ItemSearchLocationBinding.bind(view).onBindAdapter(
            position = position,
            item = item,
            currentLocation = viewModel.currentLocation,
            itemCount = searchAdapter.itemCount,
            onClick = { _, _ ->
                when (viewModel.searchType) {
                    1 -> {
                        viewModel.setFromLocationData(item)
                        viewModel.isEnableFromSearch = false
                    }
                    2 -> {
                        viewModel.setDestLocationData(item)
                        viewModel.isEnableDestSearch = false
                    }
                }
            },
            onToggleClick = {
                viewModel.toggleSaveLocation(item)
                searchAdapter.notifyItemChanged(position)
            },
            onBind = {

            }
        )
    }
}