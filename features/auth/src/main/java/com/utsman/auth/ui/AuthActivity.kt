package com.utsman.auth.ui

import android.os.Bundle
import com.utsman.auth.databinding.ActivityAuthBinding
import com.utsman.utils.BindingActivity
import com.utsman.utils.ViewPagerPage
import com.utsman.utils.setup

class AuthActivity : BindingActivity<ActivityAuthBinding>() {
    private val type: String by lazy {
        intent.getStringExtra("type") ?: "customer"
    }

    override fun inflateBinding(): ActivityAuthBinding {
        return ActivityAuthBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.vpAuth.setup(
            fragmentManager = supportFragmentManager,
            ViewPagerPage("Sign In", SignInFragment()),
            ViewPagerPage("Sign Up", SignUpFragment().apply {
                arguments?.putString("type", type)
            })
        )

        binding.tlAuth.setupWithViewPager(binding.vpAuth)
    }
}