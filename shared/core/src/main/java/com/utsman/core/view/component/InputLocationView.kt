package com.utsman.core.view.component

import android.content.Context
import android.location.Location
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.utsman.core.R
import com.utsman.core.extensions.findIdByLazy

class InputLocationView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val textViewFrom: TextView by findIdByLazy(R.id.tv_from)
    private val textViewDest: TextView by findIdByLazy(R.id.tv_destination)

    data class InputLocationData(
        val location: Location = Location(""),
        val name: String = "Select location"
    ) {
        fun isEmpty(): Boolean = name == "Select location"
    }

    private var _inputLocationFromData: InputLocationData = InputLocationData()

    var inputLocationFromData: InputLocationData
        get() = _inputLocationFromData
        set(value) {
            textViewFrom.text = value.name
            _inputLocationFromData = value
        }

    private var _inputLocationDestData: InputLocationData = InputLocationData()
    var inputLocationDestData: InputLocationData
        get() = _inputLocationDestData
        set(value) {
            textViewDest.text = value.name
            _inputLocationDestData = value
        }

    init {
        inflate(context, R.layout.component_input_location, this)

        textViewFrom.text = _inputLocationFromData.name
        textViewDest.text = _inputLocationDestData.name
    }

    companion object {
        fun locationDataEmpty() = InputLocationData()
        fun locationDataLoading() = InputLocationData(name = "Loading...")
        fun locationDataFail() = InputLocationData(name = "Failure, try again!")
    }

    fun onFromClick(action: () -> Unit) {
        textViewFrom.setOnClickListener {
            action.invoke()
        }
    }

    fun onDestClick(action: () -> Unit) {
        textViewDest.setOnClickListener {
            action.invoke()
        }
    }
}