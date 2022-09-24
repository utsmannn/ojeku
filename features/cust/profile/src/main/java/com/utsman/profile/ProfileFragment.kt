package com.utsman.profile

import android.os.Bundle
import com.utsman.profile.databinding.FragmentProfileBinding
import com.utsman.utils.BindingFragment

class ProfileFragment : BindingFragment<FragmentProfileBinding>() {
    override fun inflateBinding(): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {

    }
}