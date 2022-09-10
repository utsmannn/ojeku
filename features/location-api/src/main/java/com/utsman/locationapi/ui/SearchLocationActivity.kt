package com.utsman.locationapi.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.utsman.core.extensions.ifNetworkError
import com.utsman.core.extensions.ifStateEmpty
import com.utsman.core.state.StateEvent
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

//        viewModel.locationListLive.observe(this) {
//            println(it)
//            binding.progbar.isVisible = it is StateEvent.Loading
//            when (it) {
//                is StateEvent.Idle -> renderIdle()
//                is StateEvent.Loading -> renderLoading()
//                is StateEvent.Failure -> renderFailure(it.exception)
//                is StateEvent.Success -> renderSuccess(it.data)
//                is StateEvent.Empty -> renderEmpty()
//            }
//        }

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
            val name = binding.inputSearch.text.toString()
            viewModel.getLocations(name)
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