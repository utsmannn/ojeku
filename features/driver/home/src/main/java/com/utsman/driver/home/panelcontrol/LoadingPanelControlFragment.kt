package com.utsman.driver.home.panelcontrol

import android.os.Bundle
import com.utsman.driver.home.databinding.FragmentPanelControlLoadingBinding
import com.utsman.utils.BindingFragment

class LoadingPanelControlFragment : BindingFragment<FragmentPanelControlLoadingBinding>() {
    override fun inflateBinding(): FragmentPanelControlLoadingBinding {
        return FragmentPanelControlLoadingBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {

    }
}