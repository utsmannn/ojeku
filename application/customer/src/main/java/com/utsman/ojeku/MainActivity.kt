package com.utsman.ojeku

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.fromJson
import com.utsman.core.extensions.onFailure
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.setup
import com.utsman.navigation.activityNavigationCust
import com.utsman.ojeku.booking.UpdateLocationBooking
import com.utsman.ojeku.databinding.ActivityMainBinding
import com.utsman.ojeku.home.fragment.HomeFragment
import com.utsman.ojeku.socket.SocketWrapper
import com.utsman.utils.BindingActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

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
                } else {
                    lifecycleScope.launch {
                        setupSocket(username)
                    }
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

        CoroutineBus.getInstance().getLiveData<Boolean>("hide_navigation_menu", lifecycleScope)
            .observe(this) { isHide ->
                binding.bnMain.isVisible = !isHide
            }
    }

    private suspend fun setupSocket(username: String) {
        SocketWrapper.instance.connect()
        SocketWrapper.instance.listen(username) {
            val data = this.fromJson<UpdateLocationBooking>()
            CoroutineBus.getInstance().post("update_routes_booking", data)
        }
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.vpMain.setup(
            fragmentManager = supportFragmentManager,
            HomeFragment()
        )
    }
}