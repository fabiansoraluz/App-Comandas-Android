package com.example.project_kotlin.adaptador.vistas

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R

class VistaItemPagosTipos (itemView: View): RecyclerView.ViewHolder(itemView) {
    //Atributos
    var tvTipoPago:TextView
    var tvRecaudadoTipo:TextView
    var tvCantidadPTipos:TextView


    //Vincular
    init {
        tvTipoPago = itemView.findViewById(R.id.tvTipoPago)
        tvRecaudadoTipo = itemView.findViewById(R.id.tvRecaudadoTipo)
        tvCantidadPTipos = itemView.findViewById(R.id.tvCantidadPTipos)

    }
}