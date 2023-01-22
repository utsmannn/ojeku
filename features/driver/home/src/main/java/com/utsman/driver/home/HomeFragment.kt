package com.utsman.driver.home

import android.Manifest
import android.os.Bundle
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ojeku.profile.entity.mapToDriverExtra
import com.utsman.core.CoreDrawable
import com.utsman.core.extensions.iconResVector
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.toLatLng
import com.utsman.driver.home.databinding.FragmentHomeBinding
import com.utsman.geolib.marker.moveMarker
import com.utsman.utils.BindingFragment
import com.utsman.utils.isGrantedLocation
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BindingFragment<FragmentHomeBinding>() {

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var map: GoogleMap
    private var driverMarker: Marker? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                viewModel.getLocation()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                requestPermission()
            }
            else -> {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun inflateBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment

        mapFragment.getMapAsync {
            map = it
            map.uiSettings.isZoomControlsEnabled = true

            getLocationWithPermission()
            subscribePermission()
            subscribeProfile()
        }
    }

    private fun subscribeProfile() {
        val checkedListener =
            OnCheckedChangeListener { buttonView, isChecked ->
                viewModel.updateDriverActive(isChecked)
            }

        viewModel.getUser()
        viewModel.currentUser.observe(this) {
            it.onSuccess {
                binding.userName.text = this.username
                val extra = this.userExtra.mapToDriverExtra()
                binding.userPlat.text = extra.vehiclesNumber

                binding.toggleOn.setOnCheckedChangeListener(null)
                binding.toggleOn.isChecked = extra.isActive
                binding.toggleOn.setOnCheckedChangeListener(checkedListener)

            }
        }
    }

    private fun subscribePermission() {
        viewModel.locationResult.observe(this) {
            it.onSuccess {
                if (driverMarker == null) {
                    driverMarker = map.addMarker(
                        MarkerOptions()
                            .position(toLatLng())
                            .iconResVector(CoreDrawable.ic_marker_driver, requireContext())
                    )

                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(this.toLatLng(), 18f)
                    )
                }

                driverMarker?.moveMarker(toLatLng(), true)

                viewModel.updateLocation(this)
            }
        }
    }

    private fun getLocationWithPermission() {
        if (context?.isGrantedLocation() == false) {
            requestPermission()
        } else {
            viewModel.getLocation()
        }
    }
}