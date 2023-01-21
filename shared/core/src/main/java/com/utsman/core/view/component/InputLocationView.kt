package com.utsman.core.view.component

import android.content.Context
import android.location.Location
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.utsman.core.R
import com.utsman.core.extensions.changes
import com.utsman.core.extensions.findIdByLazy
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import java.util.concurrent.TimeUnit

class InputLocationView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val editTextViewFrom: EditText by findIdByLazy(R.id.et_from)
    private val editTextViewDest: EditText by findIdByLazy(R.id.et_destination)

    data class InputLocationData(
        val location: Location = Location(""),
        val name: String = "Find location"
    ) {
        fun isEmpty(): Boolean = name == "Select location"
    }


    private var isFromEnableTypingDefined = true
    private var isDestinationEnableTypingDefined = true

    private var _inputLocationFromData: InputLocationData = InputLocationData()
    var inputLocationFromData: InputLocationData
        get() = _inputLocationFromData
        set(value) {
            editTextViewFrom.setText(value.name)
            editTextViewFrom.alpha = if (value.name == "Find location") {
                0.4f
            } else {
                1f
            }
            editTextViewFrom.clearFocus()
            _inputLocationFromData = value
        }

    private var _inputLocationDestData: InputLocationData = InputLocationData()
    var inputLocationDestData: InputLocationData
        get() = _inputLocationDestData
        set(value) {
            editTextViewDest.setText(value.name)
            editTextViewDest.alpha = if (value.name == "Find location") {
                0.4f
            } else {
                1f
            }
            editTextViewDest.clearFocus()
            _inputLocationDestData = value
        }

    init {
        inflate(context, R.layout.component_input_location, this)

        editTextViewFrom.hint = "Find location"
        editTextViewDest.hint = "Find location"
    }

    companion object {
        fun locationDataEmpty() = InputLocationData()
        fun locationDataLoading() = InputLocationData(name = "Loading...")
        fun locationDataFail() = InputLocationData(name = "Failure, try again!")
    }

    fun onFromClick(action: () -> Unit = {}) {
        isFromEnableTypingDefined = false
        editTextViewFrom.setText(_inputLocationFromData.name)
        editTextViewFrom.isFocusable = false
        editTextViewFrom.isClickable = true
        editTextViewFrom.setOnClickListener {
            action.invoke()
        }
    }

    fun onDestClick(action: () -> Unit = {}) {
        isDestinationEnableTypingDefined = false
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

    fun textChangesFrom(action: (String) -> Unit) {
        editTextViewFrom.doOnTextChanged { text, start, before, count ->
            action.invoke(text.toString())
        }
    }

    fun textChangesDest(action: (String) -> Unit) {
        editTextViewDest.doOnTextChanged { text, start, before, count ->
            action.invoke(text.toString())
        }
    }

    fun EditText.setEnableTyping(isEnable: Boolean) {
        isFocusable = !isEnable
        isClickable = isEnable
    }

    fun textFlowFrom() =
        editTextViewFrom.changes()
            .debounce(1000)

    fun textFlowDest() =
        editTextViewDest.changes()
            .debounce(1000)
}