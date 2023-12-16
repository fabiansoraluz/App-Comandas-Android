package com.example.project_kotlin.adaptador.vistas.establecimiento

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R

class VistaItemAgregarEstablecimiento(itemView: View):RecyclerView.ViewHolder(itemView) {

    var tvIdEstablecimiento:TextView
    var tvNombre:TextView
    var tvDireccion:TextView
    var tvRuc:TextView
    var tvTelefono:TextView

    init {
        tvIdEstablecimiento=itemView.findViewById(R.id.txtcodListadoEstablecimineto)
        tvNombre=itemView.findViewById(R.id.txtnombreEstablecimientoList)
        tvDireccion=itemView.findViewById(R.id.txtdirecEstablecimientoList)
        tvRuc=itemView.findViewById(R.id.txtrucEstablecimientoList)
        tvTelefono=itemView.findViewById(R.id.txtelefonoEstablecimientoList)
    }

}