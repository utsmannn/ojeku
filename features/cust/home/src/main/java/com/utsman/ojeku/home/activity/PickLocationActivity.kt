package com.utsman.ojeku.home.activity

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.utsman.core.CoreDrawable
import com.utsman.core.CoroutineBus
import com.utsman.core.LocationManager
import com.utsman.core.extensions.*
import com.utsman.core.state.StateEvent
import com.utsman.locationapi.LocationApiLayout
import com.utsman.locationapi.databinding.ItemSearchLocationBinding
import com.utsman.locationapi.entity.LocationData
import com.utsman.locationapi.ui.onBindAdapter
import com.utsman.ojeku.home.R
import com.utsman.ojeku.home.databinding.ActivityPickLocationBinding
import com.utsman.utils.BindingActivity
import com.utsman.utils.adapter.genericAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PickLocationActivity : BindingActivity<ActivityPickLocationBinding>() {

    private val locationData: LocationData by lazy {
        val dataJson = intent.getStringExtra("saved_location")
        dataJson?.fromJson() ?: LocationData()
    }

    private lateinit var map: GoogleMap
    private var markerPick: Marker? = null

    private val viewModel: PickLocationViewModel by viewModel()

    private val locationAdapter by genericAdapter<LocationData>(
        layoutRes = LocationApiLayout.item_search_location,
        onBindItem = { position, item ->
            bindAdapter(this, position, item)
        }
    )

    override fun inflateBinding(): ActivityPickLocationBinding {
        return ActivityPickLocationBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            map.uiSettings.isZoomControlsEnabled = true

            onMapRender()
        }

        binding.etAddress.hint = "Enter address"
    }

    private fun onMapRender() {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(locationData.latLng, 14f)
        )

        if (markerPick == null) {
            val markerPickOption = MarkerOptions()
                .position(locationData.latLng)
                .iconResVector(CoreDrawable.ic_marker_green, this)

            markerPick = map.addMarker(markerPickOption)
        }

        binding.rvLocation.translationZ = 10f

        binding.etAddress.doOnTextChanged { text, start, before, count ->
            if (count >= 3) {
                viewModel.searchLocation(text.toString(), locationData.latLng)
            }
        }

        binding.rvLocation.layoutManager = LinearLayoutManager(this)
        binding.rvLocation.adapter = locationAdapter

        viewModel.locationState.observe(this) {
            binding.rvLocation.isVisible = it !is StateEvent.Idle

            it.onSuccess {
                locationAdapter.changeItem(this)
            }

            it.onLoading {
                locationAdapter.pushLoading()
            }
        }

        binding.btnConfirm.setOnClickListener {
            CoroutineBus.getInstance().post("pick_location", viewModel.pickDataLocation)
            finish()
        }
    }

    private fun bindAdapter(view: View, position: Int, item: LocationData) {
        LocationManager.instance.getLastLocation { location ->
            ItemSearchLocationBinding.bind(view).onBindAdapter(
                position = position,
                item = item,
                currentLocation = location,
                itemCount = locationAdapter.itemCount,
                onClick = { _, _ ->
                    // to marker
                    viewModel.pickDataLocation = item
                    viewModel.clearState()
                    markerPick?.position = item.latLng
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(item.latLng, 14f)
                    )
                },
                onToggleClick = {

                },
                onBind = {
                    it.imgBookmark.isVisible = false
                }
            )
        }
    }

    override fun onBackPressed() {
        if (viewModel.locationState.value !is StateEvent.Idle) {
            viewModel.clearState()
        } else {
            super.onBackPressed()
        }
    }
}