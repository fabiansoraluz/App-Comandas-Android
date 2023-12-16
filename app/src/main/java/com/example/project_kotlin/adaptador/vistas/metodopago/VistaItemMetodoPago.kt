package com.example.project_kotlin.adaptador.vistas.metodopago

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R

class VistaItemMetodoPago(itemView: View):RecyclerView.ViewHolder(itemView) {

    var tvMetPago:TextView

    init {
        tvMetPago = itemView.findViewById(R.id.tvMetPago)
    }
}