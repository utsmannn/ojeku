package com.utsman.core.view.component

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.utsman.core.R
import com.utsman.core.extensions.findIdByLazy

class TransportCardView(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {

    enum class Type {
        BIKE,
        CAR,
        TAXI
    }

    private val componentView: MaterialCardView by findIdByLazy(R.id.transport_card_root)
    private val imageView: ImageView by findIdByLazy(R.id.transport_card_img)
    private val titleView: TextView by findIdByLazy(R.id.transport_card_title)

    private var _imageRes: Int = -1
    private var _title: String = ""
    private var _isTransportSelected: Boolean = false
    private var _type: Type = Type.BIKE

    var imageRes: Int
        get() = _imageRes
        set(value) {
            setImageView(value)
        }

    var title: String
        get() = _title
        set(value) {
            titleView.text = value
        }

    var isTransportSelected: Boolean
        get() = _isTransportSelected
        set(value) {
            setBackgroundRoot(value)
        }

    var type: Type
        get() = _type
        set(value) {
            setComponentType(value)
        }

    init {
        inflate(context, R.layout.component_transport_card, this)
        context.obtainStyledAttributes(
            attributeSet, R.styleable.TransportCardView, 0, 0
        ).apply {
            val image = getResourceId(R.styleable.TransportCardView_image, -1)
            val title = getString(R.styleable.TransportCardView_title).orEmpty()
            val isSelected = getBoolean(R.styleable.TransportCardView_isSelected, false)
            val typeIndex = getInt(R.styleable.TransportCardView_transportType, _type.ordinal)
            val type = Type.values()[typeIndex]

            _title = title
            setImageView(image)
            setBackgroundRoot(isSelected)
            titleView.text = title
            setComponentType(type)

        }.recycle()
    }

    private fun setImageView(imageRes: Int) {
        _imageRes = imageRes
        if (imageRes != -1) {
            imageView.setImageResource(imageRes)
        }
    }

    private fun setBackgroundRoot(isSelected: Boolean) {
        _isTransportSelected = isSelected
        val strokeColor = if (isSelected) {
            R.color.green
        } else {
            R.color.white
        }

        componentView.strokeColor = ContextCompat.getColor(context, strokeColor)
        componentView.strokeWidth = 5
    }


    private fun setComponentType(type: Type) {
        _type = type
        val (imgRes, title) = when (type) {
            Type.BIKE -> {
                Pair(R.drawable.ic_transport_bike, "TransBike")
            }
            Type.TAXI -> {
                Pair(R.drawable.ic_transport_taxi, "TransTaxi")
            }
            Type.CAR -> {
                Pair(R.drawable.ic_transport_car, "TransCar")
            }
        }

        this.imageRes = imgRes
        this.title = title
    }
}