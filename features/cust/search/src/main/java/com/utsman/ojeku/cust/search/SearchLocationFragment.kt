package com.utsman.ojeku.cust.search

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utsman.core.LocationManager
import com.utsman.core.extensions.*
import com.utsman.core.state.StateEvent
import com.utsman.core.state.StateEventSubscriber
import com.utsman.core.view.component.InputLocationView
import com.utsman.locationapi.entity.LocationData
import com.utsman.navigation.FragmentConnector
import com.utsman.navigation.ProfileFragmentConnector
import com.utsman.navigation.replaceFragment
import com.utsman.ojeku.cust.search.databinding.FragmentSearchBinding
import com.utsman.ojeku.cust.search.databinding.ItemSearchLocationBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.adapter.genericAdapter
import com.utsman.utils.snackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

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
        layoutRes = R.layout.item_search_location,
        onBindItem = { position, item ->
            ItemSearchLocationBinding.bind(this).onBindAdapter(position, item)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocationManager.instance.getLastLocation {
            viewModel.currentLocation = it
        }
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        Toast.makeText(context, formType.toString(), Toast.LENGTH_SHORT).show()
        binding.inputSearch.setFocus(formType)

        viewModel.setFromLocationData(fromLocationExtra)
        viewModel.setDestLocationData(destLocationExtra)

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

    private fun ItemSearchLocationBinding.onBindAdapter(position: Int, item: LocationData) {
        itemTvName.text = item.name
        itemTvAddress.text = item.address

        val distance = item.latLng.toLocation().distanceTo(viewModel.currentLocation)
        val distanceIsMeters = when {
            distance < 1000.0 -> "~ ${distance.roundToInt()} M"
            else -> String.format("~ %.2f KM", distance / 1000.0)
        }

        itemTvDistance.text = distanceIsMeters

        val isLastPosition = position == searchAdapter.itemCount - 1
        itemDivider.isVisible = !isLastPosition

        root.setOnClickListener {
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
        }
    }
}