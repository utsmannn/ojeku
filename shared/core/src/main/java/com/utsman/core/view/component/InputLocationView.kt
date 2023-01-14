package com.utsman.core.view.component

import android.content.Context
import android.location.Location
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.utsman.core.R
import com.utsman.core.extensions.findIdByLazy

class InputLocationView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val editTextViewFrom: EditText by findIdByLazy(R.id.et_from)
    private val editTextViewDest: EditText by findIdByLazy(R.id.et_destination)

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
            editTextViewFrom.setText(value.name)
            _inputLocationFromData = value
        }

    private var _inputLocationDestData: InputLocationData = InputLocationData()
    var inputLocationDestData: InputLocationData
        get() = _inputLocationDestData
        set(value) {
            editTextViewDest.setText(value.name)
            _inputLocationDestData = value
        }

    init {
        inflate(context, R.layout.component_input_location, this)

        editTextViewFrom.hint = "Select location"
        editTextViewDest.hint = "Select location"
        /*editTextViewFrom.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->

            }*/
    }

    companion object {
        fun locationDataEmpty() = InputLocationData()
        fun locationDataLoading() = InputLocationData(name = "Loading...")
        fun locationDataFail() = InputLocationData(name = "Failure, try again!")
    }

    fun onFromClick(action: () -> Unit = {}) {
        editTextViewFrom.setText(_inputLocationFromData.name)

        editTextViewFrom.isFocusable = false
        editTextViewFrom.isClickable = true
        editTextViewFrom.setOnClickListener {
            action.invoke()
        }
    }

    fun onDestClick(action: () -> Unit = {}) {
        editTextViewDest.isFocusable = false
        editTextViewDest.isClickable = true
        editTextViewDest.setOnClickListener {
            action.invoke()
        }
    }

    fun setFocus(form: Int) {
        when (form) {
            1 -> {
                editTextViewFrom.isFocusable = true
                editTextViewFrom.isClickable = false
                editTextViewFrom.requestFocus()
            }
            2 -> {
                editTextViewDest.isFocusable = true
                editTextViewDest.isClickable = false
                editTextViewDest.requestFocus()
            }
        }
    }
}