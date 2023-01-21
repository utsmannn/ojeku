package com.utsman.ojeku.home.fragment

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.RoundCap
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.*
import com.utsman.core.state.StateEventSubscriber
import com.utsman.core.view.component.InputLocationView
import com.utsman.geolib.marker.clearAllLayers
import com.utsman.geolib.polyline.PolylineAnimatorBuilder
import com.utsman.geolib.polyline.data.PolylineDrawMode
import com.utsman.geolib.polyline.data.StackAnimationMode
import com.utsman.geolib.polyline.polyline.PolylineAnimator
import com.utsman.geolib.polyline.utils.createPolylineAnimatorBuilder
import com.utsman.locationapi.entity.LocationData
import com.utsman.navigation.replaceFragment
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.BookingDrawable
import com.utsman.ojeku.booking.toLocationData
import com.utsman.ojeku.home.viewmodel.HomeViewModel
import com.utsman.ojeku.home.MainActivityListener
import com.utsman.ojeku.home.R
import com.utsman.ojeku.home.databinding.FragmentHomeBinding
import com.utsman.ojeku.home.fragment.controlpanel.BookingPanelControlFragment
import com.utsman.ojeku.home.fragment.controlpanel.LoadingPanelControlFragment
import com.utsman.ojeku.home.fragment.controlpanel.LocationListPanelControlFragment
import com.utsman.ojeku.home.fragment.controlpanel.ReadyPanelControlFragment
import com.utsman.utils.BindingFragment
import com.utsman.utils.isGrantedLocation
import com.utsman.utils.listener.findActivityListener
import com.utsman.utils.snackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted

class HomeFragment : BindingFragment<FragmentHomeBinding>(), HomeFragmentListener,
    HomePanelFragmentNavigatorListener {

    companion object {
        private const val RC_LOCATION = 16
    }

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var map: GoogleMap

    private lateinit var panelTag: String

    private lateinit var animatorBuilder: PolylineAnimatorBuilder
    private lateinit var polylineAnimator: PolylineAnimator

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
            map.uiSettings.isZoomControlsEnabled = true

            animatorBuilder = map.createPolylineAnimatorBuilder(
                primaryColor = ContextCompat.getColor(requireContext(), com.utsman.core.R.color.green),
                accentColor = ContextCompat.getColor(requireContext(), com.utsman.core.R.color.white)
            ).withPrimaryPolyline {
                endCap(RoundCap())
                startCap(RoundCap())
                color(ContextCompat.getColor(requireContext(), com.utsman.core.R.color.green))
            }.withAccentPolyline {
                endCap(RoundCap())
                startCap(RoundCap())
                color(ContextCompat.getColor(requireContext(), com.utsman.core.R.color.white))
            }.withCameraAutoFocus(true)

            polylineAnimator = animatorBuilder.createPolylineAnimator()
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

        CoroutineBus.getInstance()
            .getLiveData<Pair<LocationData, LocationData>>("location_input_filled", lifecycleScope)
            .observe(this) { (from, dest) ->
                lifecycleScope.launch {
                    getActivityListener()?.sendFromLocation(from)
                    getActivityListener()?.sendDestinationLocation(dest)
                }
            }

        viewModel.filledLocationState.observe(this) { (isFilled, from, dest) ->
            if (isFilled) {
                viewModel.createBooking(from, dest)
            }
        }

        viewModel.bookingState.observe(this) {
            it.onIdle {
                navigateToLocationListFragment()
            }
            it.onLoading {
                navigateToLoading()
            }
            it.onSuccess {
                map.clear()

                when (status) {
                    Booking.BookingStatus.READY -> {
                        val currentFromLocationData = routeLocation.from.toLocationData()
                        val currentDestLocationData = routeLocation.destination.toLocationData()

                        binding.inputCardView.inputLocationFromData = InputLocationView.InputLocationData(
                            location = currentFromLocationData.latLng.toLocation(),
                            name = currentFromLocationData.name
                        )
                        binding.inputCardView.inputLocationDestData = InputLocationView.InputLocationData(
                            location = currentDestLocationData.latLng.toLocation(),
                            name = currentDestLocationData.name
                        )

                        navigateToBookingReadyFragment()
                        setupDecoratedMaps(currentFromLocationData, currentDestLocationData, this.routeLocation)
                    }
                    Booking.BookingStatus.REQUEST -> {
                        navigateToBookingFragment()
                    }
                    Booking.BookingStatus.UNDEFINE -> navigateToLocationListFragment()
                    Booking.BookingStatus.CANCELED -> {
                        viewModel.setLocationDest(LocationData())
                        navigateToLocationListFragment()
                    }
                    else -> {}
                }
            }
            it.onFailure {
                if (message.orEmpty().lowercase().contains("exist")) {
                    //binding.snackBar("$message, get current booking...")
                    viewModel.getCurrentReadyBooking()
                } else {
                    binding.snackBar(message)
                }

                navigateToLocationListFragment()
            }
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
                    name = this.name.ifEmpty {
                        "Find location"
                    }
                )

                binding.inputCardView.inputLocationFromData = locationData
            }
        }
    }

    private fun subscribeLocationDest() {
        viewModel.locationDestination.observe(this) {
            it.onSuccess {
                val locationData = InputLocationView.InputLocationData(
                    location = this.latLng.toLocation(),
                    name = this.name.ifEmpty {
                        "Find location"
                    }
                )

                binding.inputCardView.inputLocationDestData = locationData
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

    private fun setupDecoratedMaps(
        from: LocationData,
        destination: LocationData,
        routeLocation: Booking.RouteLocation
    ) {
        lifecycleScope.launch {
            val geometries = routeLocation.routes.route.map {
                LatLng(it.latitude, it.longitude)
            }

            map.addMarker(
                MarkerOptions()
                    .position(from.latLng)
                    .iconResVector(BookingDrawable.ic_marker_start, requireContext())
            )

            map.addMarker(
                MarkerOptions()
                    .position(destination.latLng)
                    .iconResVector(BookingDrawable.ic_marker_finish, requireContext())
            )

            updateMapsPadding()

            polylineAnimator.startAnimate(geometries) {
                duration = 200L
                stackAnimationMode = StackAnimationMode.WaitStackEndAnimation
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
        viewModel.setLocationFrom(from)
        viewModel.setLocationDest(destination)
    }

    override fun pushLoadingFormLocation() {
        binding.inputCardView.inputLocationFromData = InputLocationView.locationDataLoading()
        binding.inputCardView.inputLocationDestData = InputLocationView.locationDataLoading()
    }

    override fun requestInitialData(location: Location) {
        viewModel.getInitialLocation(location)
    }

    override fun navigateToLoading() {
        panelTag = childFragmentManager.replaceFragment(
            binding.framePanelControl,
            LoadingPanelControlFragment::class
        )
        updateMapsPadding()
    }

    override fun navigateToLocationListFragment() {
        panelTag = childFragmentManager.replaceFragment(
            binding.framePanelControl,
            LocationListPanelControlFragment::class
        )
        updateMapsPadding()
    }

    override fun navigateToBookingReadyFragment() {
        panelTag = childFragmentManager.replaceFragment(
            binding.framePanelControl,
            ReadyPanelControlFragment::class
        )
        updateMapsPadding()
    }

    override fun navigateToBookingFragment() {
        panelTag = childFragmentManager.replaceFragment(
            binding.framePanelControl,
            BookingPanelControlFragment::class
        )
        updateMapsPadding()
    }

    private fun updateMapsPadding() {
        lifecycleScope.launch {
            delay(200)
            map.setPadding(0, binding.inputCardView.height, 0, binding.framePanelControl.height)
        }
    }
}