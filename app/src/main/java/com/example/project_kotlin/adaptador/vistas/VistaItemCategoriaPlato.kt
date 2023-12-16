package com.example.project_kotlin.adaptador.vistas
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
class VistaItemCategoriaPlato(itemView: View):RecyclerView.ViewHolder(itemView) {
    var tvCodCategoriaPlato:TextView
    var tvNombreCategoriaPlato:TextView
    init {
        tvCodCategoriaPlato = itemView.findViewById(R.id.tvCodCategoriaPlato)
        tvNombreCategoriaPlato = itemView.findViewById(R.id.tvNombreCategoriaPlato)
    }

}