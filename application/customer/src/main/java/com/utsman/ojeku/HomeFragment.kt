package com.utsman.ojeku

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.utsman.core.extensions.toLatLng
import com.utsman.core.state.StateEventSubscriber
import com.utsman.ojeku.databinding.FragmentHomeBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.listener.findActivityListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class HomeFragment : BindingFragment<FragmentHomeBinding>(), HomeFragmentListener {

    companion object {
        private const val RC_LOCATION = 16
    }

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var map: GoogleMap

    override fun inflateBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        // start coding

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it
            getLocationWithPermission()
        }

        viewModel.subscribeLocation(locationSubscriber())
    }

    @AfterPermissionGranted(value = RC_LOCATION)
    private fun getLocationWithPermission() {
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        context?.let {
            if (EasyPermissions.hasPermissions(it, fineLocation, coarseLocation)) {
                // get location
                viewModel.getLocation()
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Granted for location",
                    RC_LOCATION,
                    fineLocation, coarseLocation
                )
            }
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

            findActivityListener<MainActivityListener>()?.onLocationResult(data)
        }
    }

    private fun actionFromActivity(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onMessageFromActivity(message: String) {
        actionFromActivity(message)
    }
}