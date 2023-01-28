package com.utsman.driver.home

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ojeku.profile.entity.User
import com.ojeku.profile.entity.mapToDriverExtra
import com.utsman.core.CoreDrawable
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.iconResVector
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.toJson
import com.utsman.core.extensions.toLatLng
import com.utsman.driver.home.databinding.DialogBookingOfferBinding
import com.utsman.driver.home.databinding.FragmentHomeBinding
import com.utsman.driver.home.panelcontrol.PickupPanelControlFragment
import com.utsman.geolib.marker.moveMarker
import com.utsman.navigation.replaceFragment
import com.utsman.network.ServiceMessage
import com.utsman.ojeku.booking.Booking
import com.utsman.ojeku.booking.rp
import com.utsman.utils.BindingFragment
import com.utsman.utils.isGrantedLocation
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
                println("Asuuuuuu -> get booking again")
                viewModel.getBooking(bookingId)
            }

        viewModel.currentBooking.observe(this) {
            it.onSuccess {
                println("asuuuuuuuu booking is -> ${this.toJson()}")
                when (status) {
                    Booking.BookingStatus.REQUEST -> {
                        viewModel.getCustomer(this.customerId)
                        showBookingOfferDialog(this)
                    }
                    Booking.BookingStatus.CANCELED -> {
                        hidePanel()
                    }
                    else -> {}
                }
            }
        }

        viewModel.acceptBooking.observe(this) {
            it.onSuccess {
                navigateToPickup()
            }
        }

        viewModel.rejectBooking.observe(this) {
            it.onSuccess {

            }
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
                    map.animateCamera(
                        CameraUpdateFactory.newLatLng(this.toLatLng())
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

    override fun hidePanel() {
        binding.framePanelControl.isVisible = false
    }

    override fun navigateToPickup() {
        binding.framePanelControl.isVisible = true
        navigateFragment(PickupPanelControlFragment::class)
    }

    override fun navigateToOngoing() {
        binding.framePanelControl.isVisible = true

    }

    override fun navigateToComplete() {
        binding.framePanelControl.isVisible = true
    }

    private fun <T: Fragment>navigateFragment(fragment: KClass<T>) {
        panelTag = childFragmentManager.replaceFragment(
            binding.framePanelControl,
            fragment
        )

        lifecycleScope.launch {
            delay(200)
            map.setPadding(0, 0, 0, binding.framePanelControl.height)
        }
    }
}