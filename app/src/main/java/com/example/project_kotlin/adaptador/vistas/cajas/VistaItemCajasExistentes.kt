package com.example.project_kotlin.adaptador.vistas.cajas

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R

class VistaItemCajasExistentes (itemView: View): RecyclerView.ViewHolder(itemView) {
    var tvIdCaja: TextView
    var tvNombEstablecimiento : TextView

    init {
        tvIdCaja = itemView.findViewById(R.id.tvIdCajaitem)
        tvNombEstablecimiento = itemView.findViewById(R.id.tvEstablecimientoitem)

    }
}