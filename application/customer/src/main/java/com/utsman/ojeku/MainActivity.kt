package com.utsman.ojeku

import android.location.Location
import android.os.Bundle
import com.utsman.navigation.attachFragment
import com.utsman.navigation.replaceFragment
import com.utsman.ojeku.cust.search.SearchLocationFragment
import com.utsman.ojeku.databinding.ActivityMainBinding
import com.utsman.utils.BindingActivity
import com.utsman.utils.listener.findFragmentListener

class MainActivity : BindingActivity<ActivityMainBinding>(), MainActivityListener {
    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var homeTag: String

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        homeTag = supportFragmentManager.attachFragment(binding.mainFrame, HomeFragment::class)
        binding.btnSearch.setOnClickListener {
            navigateToSearchFragment()
        }
    }

    private fun navigateToSearchFragment() {
        supportFragmentManager.replaceFragment(binding.mainFrame, SearchLocationFragment::class)
    }

    private fun onLocation(data: Location) {
        val instance = findFragmentListener<HomeFragmentListener>(homeTag)
        instance?.onMessageFromActivity("anuan...")
    }

    override fun onLocationResult(data: Location) {
        onLocation(data)
    }
}