package com.utsman.ojeku

import android.location.Location
import android.os.Bundle
import com.utsman.core.extensions.attachFragment
import com.utsman.locationapi.ui.SearchLocationActivity
import com.utsman.ojeku.databinding.ActivityMainBinding
import com.utsman.utils.BindingActivity
import com.utsman.utils.listener.findFragmentListener

class MainActivity : BindingActivity<ActivityMainBinding>(), MainActivityListener {
    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var homeTag: String

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        homeTag = attachFragment(binding.mainFrame, HomeFragment::class)
        binding.btnSearch.setOnClickListener {
            SearchLocationActivity.launch(this)
        }
    }

    private fun onLocation(data: Location) {
        val instance = findFragmentListener<HomeFragmentListener>(homeTag)
        instance?.onMessageFromActivity("anuan...")
    }

    override fun onLocationResult(data: Location) {
        onLocation(data)
    }
}