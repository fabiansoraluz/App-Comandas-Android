package com.example.project_kotlin.adaptador.vistas.comprobantes

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R

class VistaItemComprobante  (itemView: View): RecyclerView.ViewHolder(itemView){
    var tvCodCDP : TextView
    var tvFechaEmision : TextView
    var tvmetodoPago : TextView
    var tvcaja : TextView
    var tvtotal : TextView
    init {
        tvCodCDP = itemView.findViewById(R.id.tvCodComprobanteList)
        tvFechaEmision = itemView.findViewById(R.id.tvFechaEmisionList)
        tvmetodoPago = itemView.findViewById(R.id.tvMetPagoList)
        tvcaja = itemView.findViewById(R.id.tvCajaList)
        tvtotal = itemView.findViewById(R.id.tvTotalList)

    }

}