package com.utsman.ojekudriver

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.onFailure
import com.utsman.core.extensions.onSuccess
import com.utsman.core.extensions.setup
import com.utsman.driver.home.HomeFragment
import com.utsman.navigation.activityNavigationCust
import com.utsman.navigation.activityNavigationDriver
import com.utsman.network.ServiceMessage
import com.utsman.ojekudriver.databinding.ActivityMainBinding
import com.utsman.utils.BindingActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BindingActivity<ActivityMainBinding>() {
    private val viewModel: MainViewModel by viewModel()

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.vpMain.setup(
            fragmentManager = supportFragmentManager,
            HomeFragment()
        )

        viewModel.getCurrentUser()
        viewModel.userState.observe(this) { state ->
            state.onFailure {
                activityNavigationDriver().authActivityDriver(this@MainActivity)
                finish()
            }
            state.onSuccess {
                if (role == "CUSTOMER") {
                    activityNavigationDriver().authActivityDriver(this@MainActivity)
                    finish()
                }
            }
        }

        viewModel.fcmUpdateState.observe(this) { state ->
            state.onSuccess {
                println("fcm updated...")
            }

            state.onFailure {
                this.printStackTrace()
            }
        }

        FirebaseMessaging.getInstance()
            .token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    viewModel.updateFcmToken(task.result)
                }
            }
    }

}