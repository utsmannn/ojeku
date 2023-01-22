package com.utsman.ojeku

import android.location.Location
import android.os.Bundle
import androidx.core.os.bundleOf
import com.utsman.core.extensions.onFailure
import com.utsman.core.extensions.onSuccess
import com.utsman.locationapi.entity.LocationData
import com.utsman.navigation.activityNavigationCust
import com.utsman.navigation.replaceFragment
import com.utsman.ojeku.cust.search.SearchLocationFragment
import com.utsman.ojeku.databinding.ActivityMainBinding
import com.utsman.ojeku.home.fragment.HomeFragment
import com.utsman.ojeku.home.fragment.HomeFragmentListener
import com.utsman.ojeku.home.MainActivityListener
import com.utsman.utils.BindingActivity
import com.utsman.utils.listener.findFragmentListener
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var homeTag: String

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.getCurrentUser()
        mainViewModel.userState.observe(this) { state ->
            state.onFailure {
                activityNavigationCust().authActivityCustomer(this@MainActivity)
                finish()
            }
            state.onSuccess {
                if (role == "DRIVER") {
                    activityNavigationCust().authActivityCustomer(this@MainActivity)
                    finish()
                }
            }
        }

        mainViewModel.fcmUpdateState.observe(this) { state ->
            state.onSuccess {
                println("fcm updated...")
            }

            state.onFailure {
                this.printStackTrace()
            }
        }

        MainUtils.getFcmToken {
            mainViewModel.updateFcmToken(it)
        }
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        homeTag = supportFragmentManager.replaceFragment(binding.mainFrame, HomeFragment::class)
    }
}