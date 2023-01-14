package com.utsman.ojeku.home

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.toLatLng
import com.utsman.core.extensions.toLocation
import com.utsman.core.state.StateEventSubscriber
import com.utsman.core.view.component.InputLocationView
import com.utsman.locationapi.entity.LocationData
import com.utsman.ojeku.home.databinding.FragmentHomeBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.isGrantedLocation
import com.utsman.utils.listener.findActivityListener
import com.utsman.utils.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class HomeFragment : BindingFragment<FragmentHomeBinding>(), HomeFragmentListener {

    companion object {
        private const val RC_LOCATION = 16
    }

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var map: GoogleMap

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
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    override fun inflateBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onStart() {
        super.onStart()
        viewModel.subscribeLocation(locationSubscriber())
    }

    private fun getActivityListener(): MainActivityListener? {
        return findActivityListener()
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
        }
        subscribeLocationFrom()
        subscribeLocationDest()
        subscribeInitialLocation()
        subscribeThrowable()

        binding.inputCardView.onFromClick {
            getActivityListener()?.navigateToSearchLocation(MainActivityListener.FormType.FROM)
        }

        binding.inputCardView.onDestClick {
            getActivityListener()?.navigateToSearchLocation(MainActivityListener.FormType.DEST)
        }
    }

    @AfterPermissionGranted(value = RC_LOCATION)
    private fun getLocationWithPermission() {
        if (context?.isGrantedLocation() == false) {
            requestPermission()
        } else {
            viewModel.getLocation()
        }
    }

    private fun locationSubscriber() = object : StateEventSubscriber<Location> {
        override fun onIdle() {
            println("----- location idle")
        }

        override fun onLoading() {
            println("----- location loading")
        }

        override fun onFailure(throwable: Throwable) {
            println("----- location failure -> ${throwable.message}")
        }

        override fun onSuccess(data: Location) {
            println("----- location success -> $data")

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(data.toLatLng(), 14f)
            map.animateCamera(cameraUpdate)

            getActivityListener()?.onLocationResult(data)
        }

        override fun onEmpty() {
            println("----- location empty")
        }
    }

    private fun subscribeLocationFrom() {
        viewModel.locationFrom.observe(this) {
            it.onSuccess {
                val locationData = InputLocationView.InputLocationData(
                    location = this.latLng.toLocation(),
                    name = this.address
                )
            }
        }
    }

    private fun subscribeLocationDest() {
        viewModel.locationDestination.observe(this) {
            it.onSuccess {
                val locationData = InputLocationView.InputLocationData(
                    location = this.latLng.toLocation(),
                    name = this.address
                )
            }
        }
    }

    private fun subscribeInitialLocation() {
        viewModel.initialLocation.observe(this) {
            it.onSuccess {
                getActivityListener()?.sendFromLocation(this)
            }
        }
    }

    private fun subscribeThrowable() {
        viewModel.throwableHandler.observe(this) {
            if (it != null) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.inputCardView.inputLocationFromData = InputLocationView.locationDataFail()
                binding.inputCardView.inputLocationDestData = InputLocationView.locationDataFail()
            }
        }
    }

    private fun actionFromActivity(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onMessageFromActivity(message: String) {
        actionFromActivity(message)
    }

    override fun requestLocation() {
        getLocationWithPermission()
    }

    override fun onDataLocation(from: LocationData, destination: LocationData) {
        binding.inputCardView.inputLocationFromData = InputLocationView.InputLocationData(
            location = from.latLng.toLocation(),
            name = from.address.ifEmpty {
                "Select location"
            }
        )

        binding.inputCardView.inputLocationDestData = InputLocationView.InputLocationData(
            location = destination.latLng.toLocation(),
            name = destination.address.ifEmpty {
                "Select location"
            }
        )
    }

    override fun pushLoadingFormLocation() {
        binding.inputCardView.inputLocationFromData = InputLocationView.locationDataLoading()
        binding.inputCardView.inputLocationDestData = InputLocationView.locationDataLoading()
    }

    override fun requestInitialData(location: Location) {
        viewModel.getInitialLocation(location)
    }
}