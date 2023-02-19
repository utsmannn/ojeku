package com.utsman.auth.ui

import android.os.Bundle
import androidx.core.os.bundleOf
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
            ViewPagerPage("Sign In", SignInFragment().apply {
                arguments = bundleOf("type" to type)
            }),
            ViewPagerPage("Sign Up", SignUpFragment().apply {
                arguments = bundleOf("type" to type)
            })
        )

        binding.tlAuth.setupWithViewPager(binding.vpAuth)
    }
}