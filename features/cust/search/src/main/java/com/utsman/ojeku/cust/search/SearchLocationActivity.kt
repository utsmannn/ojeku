package com.utsman.ojeku.cust.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.utsman.core.LocationManager
import com.utsman.core.extensions.ifNetworkError
import com.utsman.core.state.StateEventSubscriber
import com.utsman.locationapi.databinding.ActivitySearchLocationBinding
import com.utsman.locationapi.entity.LocationData
import com.utsman.utils.BindingActivity
import org.koin.android.ext.android.inject

class SearchLocationActivity : BindingActivity<ActivitySearchLocationBinding>() {

    private val viewModel: SearchLocationViewModel by inject()

    override fun inflateBinding(): ActivitySearchLocationBinding {
        return ActivitySearchLocationBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {


        binding.btnSearch.setOnClickListener {
            val name = binding.inputSearch.text.toString()
            LocationManager.instance.getLastLocation { location ->
                viewModel.getLocations(name, location)
            }
        }
    }

    private fun renderLoading() {
        binding.progbar.isVisible = true
    }

    private fun renderIdle() {
        //
        binding.progbar.isVisible = false
    }

    private fun renderSuccess(data: List<LocationData>) {
        binding.progbar.isVisible = false
        binding.txtResult.text = data.map { l -> l.name }.toString()
    }

    private fun renderFailure(throwable: Throwable) {
        /*throwable.ifStateEmpty {
            binding.txtResult.text = "Kosong"
        }*/
        binding.progbar.isVisible = false

        throwable.ifNetworkError {
            binding.txtResult.text = throwable.message
        }
    }

    private fun renderEmpty() {
        binding.progbar.isVisible = false
        binding.txtResult.text = "Kosong"
    }

    companion object {
        @JvmStatic
        fun launch(context: Context) {
            context.startActivity(
                Intent(context, SearchLocationActivity::class.java)
            )
        }
    }
}