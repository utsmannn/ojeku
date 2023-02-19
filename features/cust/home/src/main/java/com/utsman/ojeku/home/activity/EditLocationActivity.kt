package com.utsman.ojeku.home.activity

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.utsman.core.CoroutineBus
import com.utsman.core.extensions.IOScope
import com.utsman.core.extensions.fromJson
import com.utsman.core.extensions.toJson
import com.utsman.locationapi.entity.LocationData
import com.utsman.navigation.intentTo
import com.utsman.ojeku.home.databinding.ActivityEditLocationBinding
import com.utsman.utils.BindingActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditLocationActivity : BindingActivity<ActivityEditLocationBinding>() {

    private val viewModel: EditLocationViewModel by viewModel()

    private val locationData: LocationData by lazy {
        val dataJson = intent.getStringExtra("saved_location")
        dataJson?.fromJson() ?: LocationData()
    }

    override fun inflateBinding(): ActivityEditLocationBinding {
        return ActivityEditLocationBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        viewModel.savedLocation = locationData

        binding.etName.setText(locationData.name)
        binding.etName.hint = locationData.name

        binding.etAddress.setText(locationData.address)
        binding.etAddress.hint = locationData.address

        binding.etAddress.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                intentTo(PickLocationActivity::class) {
                    it.putExtra("saved_location", locationData.toJson())
                }
                v.clearFocus()
            }
        }

        CoroutineBus.getInstance().getLiveData<LocationData>("pick_location", lifecycleScope)
            .observe(this) {
                binding.etAddress.setText(it.address)
            }
    }

    override fun onResume() {
        super.onResume()
        binding.etAddress.clearFocus()
    }

    override fun onPause() {
        lifecycleScope.launch {
            viewModel.savedLocation.name = binding.etName.text.toString()
            viewModel.savedLocation.address = binding.etAddress.text.toString()
            viewModel.editLocationData()
        }
        super.onPause()
    }
}