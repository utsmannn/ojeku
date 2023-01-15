package com.utsman.ojeku

import android.location.Location
import android.os.Bundle
import androidx.core.os.bundleOf
import com.utsman.core.extensions.onFailure
import com.utsman.locationapi.entity.LocationData
import com.utsman.navigation.activityNavigationCust
import com.utsman.navigation.attachFragment
import com.utsman.navigation.replaceFragment
import com.utsman.ojeku.cust.search.SearchLocationFragment
import com.utsman.ojeku.databinding.ActivityMainBinding
import com.utsman.ojeku.home.HomeFragment
import com.utsman.ojeku.home.HomeFragmentListener
import com.utsman.ojeku.home.MainActivityListener
import com.utsman.utils.BindingActivity
import com.utsman.utils.listener.findFragmentListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BindingActivity<ActivityMainBinding>(), MainActivityListener {
    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var homeTag: String

    private var fromLocation: LocationData = LocationData()
    private var destLocation: LocationData = LocationData()

    private var currentLocation: Location? = null

    private val mainViewModel: MainViewModel by viewModel()

    private fun getFragmentListener(): HomeFragmentListener? {
        return findFragmentListener(homeTag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.getCurrentUser()
        mainViewModel.userState.observe(this) { state ->
            println("OJEKU =======")
            println(state)
            println("OJEKU =======")
            state.onFailure {
                activityNavigationCust().authActivity(this@MainActivity)
                finish()
            }
        }
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        homeTag = supportFragmentManager.attachFragment(binding.mainFrame, HomeFragment::class)
        MainScope().launch {
            delay(1000)
            getFragmentListener()?.pushLoadingFormLocation()
            getFragmentListener()?.requestLocation()
        }
    }

    private fun navigateToSearchFragment() {
        supportFragmentManager.replaceFragment(binding.mainFrame, SearchLocationFragment::class)
    }

    override fun onLocationResult(data: Location) {
        if (currentLocation != data) {
            currentLocation = data
            getFragmentListener()?.requestInitialData(data)
        }
    }

    override fun sendFromLocation(from: LocationData) {
        fromLocation = from
        updateLocationData()
    }

    override fun sendDestinationLocation(destination: LocationData) {
        destLocation = destination
        updateLocationData()
    }

    override fun navigateToSearchLocation(formType: Int) {
        val bundleForm = bundleOf(
            "formType" to formType,
            "location_from" to fromLocation,
            "location_dest" to destLocation
        )
        supportFragmentManager.replaceFragment(binding.mainFrame, SearchLocationFragment::class, bundle = bundleForm)
    }

    private fun updateLocationData() {
        getFragmentListener()?.onDataLocation(fromLocation, destLocation)
    }
}