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
import com.google.android.gms.maps.model.*
import com.utsman.core.CoreDrawable
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.*
import com.utsman.core.state.StateEventSubscriber
import com.utsman.core.view.component.InputLocationView
import com.utsman.geolib.marker.moveMarker
import com.utsman.geolib.polyline.PolylineAnimatorBuilder
import com.utsman.geolib.polyline.data.StackAnimationMode
import com.utsman.geolib.polyline.polyline.PolylineAnimator
import com.utsman.geolib.polyline.utils.createPolylineAnimatorBuilder
import com.utsman.locationapi.entity.LocationData
import com.utsman.navigation.activityNavigationCust
import com.utsman.navigation.replaceFragment
import com.utsman.network.ServiceMessage
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.BookingDrawable
import com.utsman.ojeku.booking.UpdateLocationBooking
import com.utsman.ojeku.booking.toLocationData
import com.utsman.ojeku.home.R
import com.utsman.ojeku.home.databinding.FragmentHomeBinding
import com.utsman.ojeku.home.fragment.controlpanel.*
import com.utsman.ojeku.home.viewmodel.HomeViewModel
import com.utsman.utils.BindingFragment
import com.utsman.utils.isGrantedLocation
import com.utsman.utils.orNol
import com.utsman.utils.snackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted

class HomeFragment : BindingFragment<FragmentHomeBinding>(), /*HomeFragmentListener,*/
    HomePanelFragmentNavigatorListener {

    companion object {
        private const val RC_LOCATION = 16
    }

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var map: GoogleMap

    private lateinit var panelTag: String

    private lateinit var animatorBuilder: PolylineAnimatorBuilder
    private lateinit var polylineAnimator: PolylineAnimator

    private var fromLocation: LocationData = LocationData()
    private var destLocation: LocationData = LocationData()
    private var isSkipLoading = false

    private var driverMarker: Marker? = null
    private var otherMarker: Marker? = null
    private var currentPolyline: Polyline? = null

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
            viewModel.subscribeLocation(locationSubscriber())

            binding.inputCardView.inputLocationFromData = InputLocationView.locationDataLoading()
            binding.inputCardView.inputLocationDestData = InputLocationView.locationDataLoading()

            animatorBuilder = map.createPolylineAnimatorBuilder(
                primaryColor = ContextCompat.getColor(
                    requireContext(),
                    com.utsman.core.R.color.green
                ),
                accentColor = ContextCompat.getColor(
                    requireContext(),
                    com.utsman.core.R.color.white
                )
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
            activityNavigationCust().searchLocationActivity(context) {
                it.putExtra("formType", 1)
                it.putExtra("location_from", fromLocation)
                it.putExtra("location_dest", destLocation)
            }
        }

        binding.inputCardView.onDestClick {
            activityNavigationCust().searchLocationActivity(context) {
                it.putExtra("formType", 2)
                it.putExtra("location_from", fromLocation)
                it.putExtra("location_dest", destLocation)
            }
        }

        CoroutineBus.getInstance()
            .getLiveData<Pair<LocationData, LocationData>>("location_input_filled", lifecycleScope)
            .observe(this) { (from, dest) ->
                viewModel.setLocationFrom(from)
                viewModel.setLocationDest(dest)
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
                if (!isSkipLoading) {
                    navigateToLoading()
                }
            }
            it.onSuccess {
                map.clear()
                driverMarker?.remove()
                driverMarker = null
                currentPolyline = null

                isSkipLoading = this.status == Booking.BookingStatus.REQUEST_RETRY
                when (status) {
                    Booking.BookingStatus.READY -> {
                        val currentFromLocationData = routeLocation.from.toLocationData()
                        val currentDestLocationData = routeLocation.destination.toLocationData()

                        binding.inputCardView.inputLocationFromData =
                            InputLocationView.InputLocationData(
                                location = currentFromLocationData.latLng.toLocation(),
                                name = currentFromLocationData.name
                            )
                        binding.inputCardView.inputLocationDestData =
                            InputLocationView.InputLocationData(
                                location = currentDestLocationData.latLng.toLocation(),
                                name = currentDestLocationData.name
                            )

                        navigateToBookingReadyFragment()
                        setupDecoratedMaps(
                            currentFromLocationData,
                            currentDestLocationData,
                            this.routeLocation
                        )
                    }
                    Booking.BookingStatus.REQUEST -> {
                        navigateToBookingFragment()
                    }
                    Booking.BookingStatus.UNDEFINE -> {
                        navigateToLocationListFragment()
                    }
                    Booking.BookingStatus.CANCELED -> {
                        viewModel.setLocationDest(LocationData())
                        viewModel.clearEstimatedDuration()
                        navigateToLocationListFragment()
                    }
                    Booking.BookingStatus.REQUEST_RETRY -> {
                        viewModel.retryBooking()
                    }
                    Booking.BookingStatus.ACCEPTED -> {
                        navigateToPickupOngoingFragment()
                    }
                    Booking.BookingStatus.ONGOING -> {
                        navigateToPickupOngoingFragment()
                    }
                    else -> {}
                }
            }
            it.onFailure {
                if (message.orEmpty().lowercase().contains("exist")) {
                    viewModel.getCurrentReadyBooking()
                } else {
                    binding.snackBar(message)
                }

                this.printStackTrace()
                navigateToLocationListFragment()
            }
        }

        CoroutineBus.getInstance()
            .getLiveData<ServiceMessage>(
                ServiceMessage.Type.BOOKING_UNAVAILABLE.name,
                lifecycleScope
            )
            .observe(this) {
                viewModel.cancelCurrentReadyBookingByServices(it)
            }

        CoroutineBus.getInstance()
            .getLiveData<ServiceMessage>(ServiceMessage.Type.BOOKING.name, lifecycleScope)
            .observe(this) {
                isSkipLoading = true
                viewModel.getBooking(it.bookingId)
            }

        CoroutineBus.getInstance()
            .getLiveData<UpdateLocationBooking>("update_routes_booking", lifecycleScope)
            .observe(this) {
                /*val currentBooking = viewModel.bookingState.value?.value
                if (currentBooking != null && currentBooking.status == Booking.BookingStatus.ACCEPTED) {
                    setupDecoratedMapsBookingAcceptedOnGoing(currentBooking, it)
                }*/

                val currentBooking = viewModel.bookingState.value?.value
                val isStatusValid = when (currentBooking?.status) {
                    Booking.BookingStatus.ACCEPTED, Booking.BookingStatus.ONGOING -> true
                    else -> false
                }
                if (currentBooking != null && isStatusValid) {
                    setupDecoratedMapsBookingAcceptedOnGoing(currentBooking, it)
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

            viewModel.getInitialLocation(data)
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
                fromLocation = this
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
                destLocation = this
            }
        }
    }

    private fun subscribeInitialLocation() {
        viewModel.initialLocation.observe(this) {
            it.onSuccess {
                viewModel.setLocationFrom(this)
                viewModel.setLocationDest(LocationData())
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
                stackAnimationMode = StackAnimationMode.BlockStackAnimation
            }
        }
    }

    private fun setupDecoratedMapsBookingAcceptedOnGoing(booking: Booking, updateLocationBooking: UpdateLocationBooking) {
        val (destinationCoordinate, otherMarkerVector) = when (booking.status) {
            Booking.BookingStatus.ACCEPTED -> {
                Pair(booking.routeLocation.from.coordinate, BookingDrawable.ic_marker_start)
            }
            Booking.BookingStatus.ONGOING -> {
                Pair(booking.routeLocation.destination.coordinate, BookingDrawable.ic_marker_finish)
            }
            else -> return
        }

        val driverCoordinate = updateLocationBooking.driver.toCoordinate()
        val geometries = updateLocationBooking.route?.toRoutes()?.route.orEmpty().map { coor ->
            LatLng(coor.latitude, coor.longitude)
        }

        val durationEstimated = updateLocationBooking.route?.durationEstimated.orNol()
        if (durationEstimated > 1) {
            val baseDuration = durationEstimated / 60L
            val durationText = if (baseDuration - 2 >= 1) {
                val estimated = baseDuration - 2
                "Estimated time $estimated - $baseDuration min"
            } else {
                "Estimated time ~ $baseDuration min"
            }

            viewModel.updateEstimatedDuration(durationText)
        }

        if (driverMarker == null) {
            driverMarker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(driverCoordinate.latitude, driverCoordinate.longitude))
                    .iconResVector(CoreDrawable.ic_marker_driver, requireContext())
            )

            otherMarker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(destinationCoordinate.latitude, destinationCoordinate.longitude))
                    .iconResVector(otherMarkerVector, requireContext())
            )

        } else {
            driverMarker?.moveMarker(LatLng(driverCoordinate.latitude, driverCoordinate.longitude), true)
        }

        if (currentPolyline == null) {
            currentPolyline = map.addPolyline(
                PolylineOptions()
                    .addAll(geometries)
                    .width(8f)
                    .color(ContextCompat.getColor(requireContext(), com.utsman.core.R.color.green))
                    .endCap(RoundCap())
                    .startCap(RoundCap())
            )

            val boundingBox = LatLngBounds.builder()
                .also {
                    geometries.forEach { geo ->
                        it.include(geo)
                    }
                }
                .build()

            map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(boundingBox, 12)
            )
        } else {
            currentPolyline?.points = geometries
        }
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

    override fun navigateToPickupOngoingFragment() {
        panelTag = childFragmentManager.replaceFragment(
            binding.framePanelControl,
            PickupOngoingPanelControlFragment::class
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