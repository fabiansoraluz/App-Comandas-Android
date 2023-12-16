package com.example.project_kotlin.adaptador.vistas.cajas

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R

class VistaItemCaja (itemView: View): RecyclerView.ViewHolder(itemView) {
    //Atributos
    var tvNumRecibo:TextView
    var tvDNI:TextView
    var tvNumComanda:TextView
    var tvObtenido:TextView

    //Vincular
    init {
        tvNumRecibo = itemView.findViewById(R.id.tvNumRecibo)
        tvDNI = itemView.findViewById(R.id.tvDNI)
        tvNumComanda = itemView.findViewById(R.id.tvNumComanda)
        tvObtenido = itemView.findViewById(R.id.tvObtenido)
    }
}