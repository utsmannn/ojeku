package com.utsman.ojeku.home.fragment.controlpanel

import android.os.Bundle
import com.utsman.ojeku.home.databinding.FragmentPanelControlLoadingBinding
import com.utsman.utils.BindingFragment

class LoadingPanelControlFragment : BindingFragment<FragmentPanelControlLoadingBinding>() {
    override fun inflateBinding(): FragmentPanelControlLoadingBinding {
        return FragmentPanelControlLoadingBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {

    }
}