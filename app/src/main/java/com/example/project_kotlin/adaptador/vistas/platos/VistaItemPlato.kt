package com.example.project_kotlin.adaptador.vistas.platos

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R

class VistaItemPlato(itemView: View):RecyclerView.ViewHolder(itemView) {

    var tvCodPlato:TextView
    var tvNombrePlato:TextView
    var tvPrecioPlato:TextView
    var tvCatNomPlato:TextView
    var imagenPlato:ImageView
    init {
        tvCodPlato = itemView.findViewById(R.id.tvCodPlatos)
        tvNombrePlato = itemView.findViewById(R.id.tvNombrePlatos)
        tvPrecioPlato = itemView.findViewById(R.id.tvPrecioPlatos)
        tvCatNomPlato = itemView.findViewById(R.id.tvCatNomPlatos)
        imagenPlato = itemView.findViewById(R.id.imageplato)
    }
}