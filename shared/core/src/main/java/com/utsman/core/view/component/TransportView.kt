package com.utsman.core.view.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.utsman.core.R
import com.utsman.core.extensions.findIdByLazy

class TransportView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val rvTransport: RecyclerView by findIdByLazy(R.id.rv_transport)

    private var currentPosition: Int = -1

    init {
        inflate(context, R.layout.component_transport_view, this)

        val transportAdapter = RvAdapter()
        rvTransport.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rvTransport.adapter = transportAdapter
    }

    private inner class RvAdapterViewHolder(view: View) : ViewHolder(view)

    private inner class RvAdapter : RecyclerView.Adapter<RvAdapterViewHolder>() {
        private val list = listOf(
            TransportCardView.Type.BIKE,
            TransportCardView.Type.CAR,
            TransportCardView.Type.TAXI,
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvAdapterViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_component_transport_view, parent, false)
            return RvAdapterViewHolder(view)
        }

        override fun onBindViewHolder(holder: RvAdapterViewHolder, position: Int) {
            val transportCard = holder.itemView.findViewById<TransportCardView>(R.id.transport_card)

            if (position > 0) {
                transportCard.updateLayoutParams<MarginLayoutParams> {
                    updateMargins(left = -50 - (12*4))
                }
            }
            transportCard.type = list[position]

            transportCard.isTransportSelected = position == currentPosition

            transportCard.setOnClickListener {
                val savedPosition = position
                if (currentPosition != position) {
                    currentPosition = savedPosition
                    notifyDataSetChanged()
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }
}