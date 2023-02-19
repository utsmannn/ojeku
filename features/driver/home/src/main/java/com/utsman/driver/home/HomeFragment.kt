package com.utsman.driver.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.ojeku.profile.entity.mapToDriverExtra
import com.utsman.core.CoreDrawable
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.*
import com.utsman.driver.home.databinding.DialogBookingOfferBinding
import com.utsman.driver.home.databinding.FragmentHomeBinding
import com.utsman.driver.home.panelcontrol.DonePanelControlFragment
import com.utsman.driver.home.panelcontrol.LoadingPanelControlFragment
import com.utsman.driver.home.panelcontrol.PickupPanelControlFragment
import com.utsman.driver.home.panelcontrol.OngoingPanelControlFragment
import com.utsman.geolib.marker.moveMarker
import com.utsman.navigation.replaceFragment
import com.utsman.network.ServiceMessage
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.BookingDrawable
import com.utsman.ojeku.booking.UpdateLocationBooking
import com.utsman.ojeku.booking.rp
import com.utsman.utils.BindingFragment
import com.utsman.utils.isGrantedLocation
import com.utsman.utils.snackBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt
import kotlin.reflect.KClass

class HomeFragment : BindingFragment<FragmentHomeBinding>(), HomePanelFragmentNavigatorListener {

    private val viewModel: HomeViewModel by viewModel()

    private lateinit var panelTag: String
    private lateinit var map: GoogleMap

    private var driverMarker: Marker? = null
    private var otherMarker: Marker? = null

    private var currentPolyline: Polyline? = null

    private val dialogBookingOfferBinding: DialogBookingOfferBinding by lazy {
        DialogBookingOfferBinding.bind(
            LayoutInflater.from(context).inflate(R.layout.dialog_booking_offer, null)
        )
    }

    private val dialogOffer: AlertDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setView(dialogBookingOfferBinding.root)
            .setCancelable(false)
            .create()
    }

    private var isDialogOpen = false

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

        hidePanel()
        mapFragment.getMapAsync {
            map = it
            map.uiSettings.isZoomControlsEnabled = true

            getLocationWithPermission()
            subscribePermission()
            subscribeProfile()
        }

        CoroutineBus.getInstance()
            .getLiveData<ServiceMessage>(ServiceMessage.Type.BOOKING.name, lifecycleScope)
            .observe(this) { serviceMessage ->
                val bookingId = serviceMessage.bookingId
                viewModel.getBooking(bookingId)
            }

        CoroutineBus.getInstance()
            .getLiveData<ServiceMessage>(ServiceMessage.Type.BOOKING_LOCATION.name, lifecycleScope)
            .observe(this) { serviceMessage ->
                val currentBooking = viewModel.currentBooking.value?.value
                if (currentBooking != null) {
                    when (currentBooking.status) {
                        Booking.BookingStatus.ONGOING -> {
                            binding.snackBar("on going location")
                        }
                        else -> {}
                    }
                }
            }

        CoroutineBus.getInstance()
            .getLiveData<ServiceMessage>(ServiceMessage.Type.BOOKING_REQUEST.name, lifecycleScope)
            .observe(this) { serviceMessage ->
                binding.snackBar("booking incoming....")
                viewModel.isDisableFragmentLoading = true

                val bookingId = serviceMessage.bookingId
                viewModel.getBooking(bookingId)
                viewModel.currentBookingId = serviceMessage.bookingId
            }

        CoroutineBus.getInstance()
            .getLiveData<UpdateLocationBooking>("update_routes_booking", lifecycleScope)
            .observe(this) {
                val currentBooking = viewModel.currentBooking.value?.value
                val isStatusValid = when (currentBooking?.status) {
                    Booking.BookingStatus.ACCEPTED, Booking.BookingStatus.ONGOING -> true
                    else -> false
                }
                if (currentBooking != null && isStatusValid) {
                    setupDecorationMapsAcceptedOnGoing(currentBooking, it)
                }
            }

        viewModel.doneUiState.observe(this@HomeFragment) { isDone ->
            if (isDone) {
                navigateToDone()
            } else {
                hidePanel()
            }
        }

        viewModel.currentBooking.observe(this) {
            it.onLoading {
                viewModel.showDonePanel(false)
                if (!viewModel.isDisableFragmentLoading) {
                    navigateToLoading()
                }
            }
            it.onSuccess {
                viewModel.isDisableFragmentLoading = false
                viewModel.currentStatusBooking = this.status
                hidePanel()
                when (status) {
                    Booking.BookingStatus.REQUEST -> {
                        viewModel.getCustomer(this.customerId)
                        showBookingOfferDialog(this)
                    }
                    Booking.BookingStatus.CANCELED -> {
                        clearMaps()
                    }
                    Booking.BookingStatus.ACCEPTED -> {
                        navigateToPickup()
                    }
                    Booking.BookingStatus.ONGOING -> {
                        clearMaps()
                        navigateToOngoing()
                    }
                    Booking.BookingStatus.DONE -> {
                        clearMaps()
                        viewModel.showDonePanel(true)
                    }
                    else -> {}
                }
            }
            it.onFailure {
                if (viewModel.currentStatusBooking == Booking.BookingStatus.ACCEPTED) {
                    binding.snackBar(this.message)
                    val currentBookingId = viewModel.currentBookingId
                    if (currentBookingId.isEmpty()) {
                        hidePanel()
                    } else {
                        viewModel.getBooking(viewModel.currentBookingId)
                    }
                } else {
                    viewModel.currentStatusBooking = Booking.BookingStatus.UNDEFINE
                    hidePanel()
                }
            }
        }

        viewModel.rejectBooking.observe(this) {
            it.onSuccess {

            }
        }
    }

    private fun clearMaps() {
        otherMarker?.remove()
        otherMarker = null

        currentPolyline?.remove()
        currentPolyline = null
        viewModel.clearPickupRoute()
    }

    private fun setupDecorationMapsAcceptedOnGoing(
        booking: Booking,
        updateLocationBooking: UpdateLocationBooking
    ) {
        val (destinationCoordinate, otherMarkerVector) = when (booking.status) {
            Booking.BookingStatus.ACCEPTED -> {
                Pair(booking.routeLocation.from.coordinate, BookingDrawable.ic_marker_start)
            }
            Booking.BookingStatus.ONGOING -> {
                Pair(booking.routeLocation.destination.coordinate, BookingDrawable.ic_marker_finish)
            }
            else -> return
        }

        val route = updateLocationBooking.route?.toRoutes()

        if (route != null) {
            viewModel.updatePickupRoute(route)
        }

        val driverCoordinate = updateLocationBooking.driver.toCoordinate()
        val geometries = updateLocationBooking.route?.toRoutes()?.route.orEmpty().map { coor ->
            LatLng(coor.latitude, coor.longitude)
        }

        if (driverMarker == null) {
            driverMarker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(driverCoordinate.latitude, driverCoordinate.longitude))
                    .iconResVector(CoreDrawable.ic_marker_driver, requireContext())
            )

        } else {
            driverMarker?.moveMarker(
                LatLng(driverCoordinate.latitude, driverCoordinate.longitude),
                true
            )
        }

        if (currentPolyline == null) {
            otherMarker = map.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            destinationCoordinate.latitude,
                            destinationCoordinate.longitude
                        )
                    )
                    .iconResVector(otherMarkerVector, requireContext())
            )

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

    private fun showBookingOfferDialog(booking: Booking) {
        dialogBookingOfferBinding.tvPrice.text = booking.price.rp()
        val textFrom = booking.routeLocation.from.name
        val textDest = booking.routeLocation.destination.name
        val distance = booking.routeLocation.routes.distance.toFloat()
        val distanceIsMeters = when {
            distance < 1000.0 -> "~ ${distance.roundToInt()} M"
            else -> String.format("~ %.2f KM", distance / 1000.0)
        }

        dialogBookingOfferBinding.tvCustAddress.text = textFrom
        dialogBookingOfferBinding.tvFrom.text = textFrom
        dialogBookingOfferBinding.tvDest.text = textDest
        dialogBookingOfferBinding.tvDistance.text = distanceIsMeters

        viewModel.customerUser.observe(this) {
            it.onSuccess {
                dialogBookingOfferBinding.tvCustName.text = username
            }
        }

        dialogBookingOfferBinding.btnIgnore.setOnClickListener {
            viewModel.rejectBooking(booking.id)
            dialogOffer.dismiss()
        }

        dialogBookingOfferBinding.btnAccept.setOnClickListener {
            viewModel.acceptBooking(booking.id)
            dialogOffer.dismiss()
        }

        dialogOffer.setOnShowListener {
            isDialogOpen = true
        }

        dialogOffer.setOnDismissListener {
            isDialogOpen = false
        }

        if (!dialogOffer.isShowing) {
            dialogOffer.show()
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
                } else {
                    if (map.cameraPosition.zoom < 12f) {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(this.toLatLng(), 18f)
                        )
                    } else {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLng(this.toLatLng())
                        )
                    }
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

    override fun hidePanel() {
        binding.framePanelControl.isVisible = false
    }

    override fun navigateToLoading() {
        binding.framePanelControl.isVisible = true
        navigateFragment(LoadingPanelControlFragment::class)
    }

    override fun navigateToPickup() {
        binding.framePanelControl.isVisible = true
        saveAndRestoreMargin()
        navigateFragment(PickupPanelControlFragment::class)
    }

    override fun navigateToOngoing() {
        binding.framePanelControl.isVisible = true
        saveAndRestoreMargin()
        navigateFragment(OngoingPanelControlFragment::class)
    }

    override fun navigateToComplete() {
        binding.framePanelControl.isVisible = true
    }

    override fun navigateToDone() {
        binding.framePanelControl.isVisible = true
        binding.framePanelControl.updateLayoutParams<MarginLayoutParams> {
            height = ConstraintLayout.LayoutParams.MATCH_PARENT
            updateMargins(0, 0, 0, 0)
        }
        navigateFragment(DonePanelControlFragment::class)
    }

    private fun saveAndRestoreMargin() {
        val currentMargin = viewModel.savedMargin
        if (currentMargin != PanelMargin()) {
            binding.framePanelControl.updateLayoutParams<MarginLayoutParams> {
                height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                val panelMargin = PanelMargin(
                    top = topMargin,
                    bottom = bottomMargin,
                    start = leftMargin,
                    end = rightMargin
                )
                viewModel.savedMargin = panelMargin
            }
        } else {
            binding.framePanelControl.updateLayoutParams<MarginLayoutParams> {
                height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                val panelMargin = viewModel.savedMargin
                updateMargins(
                    left = panelMargin.start,
                    right = panelMargin.end,
                    top = panelMargin.top,
                    bottom = panelMargin.bottom
                )
            }
        }
    }

    private fun <T : Fragment> navigateFragment(fragment: KClass<T>) {
        panelTag = childFragmentManager.replaceFragment(
            binding.framePanelControl,
            fragment
        )

        lifecycleScope.launch {
            delay(500)
            map.setPadding(0, 0, 0, binding.framePanelControl.height + 24 * 3)
        }
    }
}