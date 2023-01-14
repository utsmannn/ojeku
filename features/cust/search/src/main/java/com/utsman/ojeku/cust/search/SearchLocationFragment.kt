package com.utsman.ojeku.cust.search

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.utsman.core.extensions.ifNetworkError
import com.utsman.core.extensions.toLocation
import com.utsman.core.state.StateEventSubscriber
import com.utsman.core.view.component.InputLocationView
import com.utsman.locationapi.entity.LocationData
import com.utsman.navigation.FragmentConnector
import com.utsman.navigation.ProfileFragmentConnector
import com.utsman.navigation.replaceFragment
import com.utsman.ojeku.cust.search.databinding.FragmentSearchBinding
import com.utsman.utils.BindingFragment
import com.utsman.utils.snackBar
import org.koin.android.ext.android.inject

class SearchLocationFragment : BindingFragment<FragmentSearchBinding>() {

    private val viewModel: SearchLocationViewModel by inject()

    override fun inflateBinding(): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(layoutInflater)
    }

    private val formType: Int by lazy {
        arguments?.getInt("formType", 1) ?: 1
    }

    private val fromLocationExtra by lazy {
        arguments?.getParcelable("location_from") ?: LocationData()
    }

    private val destLocationExtra by lazy {
        arguments?.getParcelable("location_dest") ?: LocationData()
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        Toast.makeText(context, formType.toString(), Toast.LENGTH_SHORT).show()
        binding.inputSearch.setFocus(formType)

        viewModel.fromLocation = fromLocationExtra
        viewModel.destLocation = destLocationExtra

        binding.snackBar(viewModel.fromLocation.name)
        if (viewModel.fromLocation.latLng.latitude != 0.0) {
            binding.inputSearch.inputLocationFromData = InputLocationView.InputLocationData(
                location = viewModel.fromLocation.latLng.toLocation(),
                name = viewModel.fromLocation.address
            )
        }

        if (viewModel.destLocation.latLng.latitude != 0.0) {
            binding.inputSearch.inputLocationDestData = InputLocationView.InputLocationData(
                location = viewModel.destLocation.latLng.toLocation(),
                name = viewModel.destLocation.address
            )
        }

        viewModel.subscribeLocationStateManager(object : StateEventSubscriber<List<LocationData>> {
            override fun onIdle() {
                renderIdle()
            }

            override fun onLoading() {
                renderLoading()
            }

            override fun onFailure(throwable: Throwable) {
                renderFailure(throwable)
            }

            override fun onSuccess(data: List<LocationData>) {
                renderSuccess(data)
            }

            override fun onEmpty() {
                renderEmpty()
            }

        })

        binding.btnSearch.setOnClickListener {
            //val name = binding.inputSearch.text.toString()
            //viewModel.getLocations(name)
        }
        binding.btnProfile.setOnClickListener {
            val profileFragment = FragmentConnector.Profile.profileFragment
            childFragmentManager.replaceFragment(binding.frameLayout, profileFragment)
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
}