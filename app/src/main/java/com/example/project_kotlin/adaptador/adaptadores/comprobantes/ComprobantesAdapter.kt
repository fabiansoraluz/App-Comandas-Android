package com.example.project_kotlin.adaptador.adaptadores.comprobantes

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.vistas.VistaItemCategoriaPlato
import com.example.project_kotlin.adaptador.vistas.comandas.VistaItemComanda
import com.example.project_kotlin.adaptador.vistas.comprobantes.VistaItemComprobante
import com.example.project_kotlin.entidades.CategoriaPlato
import com.example.project_kotlin.entidades.ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.categoria_platos.EditCatPlatoActivity
import com.example.project_kotlin.vistas.facturar.DetallesComprobante

class ComprobantesAdapter (var info :  List<ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago>)
    : RecyclerView.Adapter<VistaItemComprobante>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemComprobante {
        val vista = LayoutInflater.from(parent.context).
        inflate(R.layout.item_comprobante,parent,false)
        return VistaItemComprobante(vista)
    }

    override fun getItemCount(): Int = info.size

    override fun onBindViewHolder(holder: VistaItemComprobante, position: Int) {
        holder.tvCodCDP.text = info.get(position).comprobante.comprobante.comprobante.comprobante.comprobante.id.toString()
        holder.tvFechaEmision.text = info.get(position).comprobante.comprobante.comprobante.comprobante.comprobante.fechaEmision.toString()
        holder.tvcaja.text = info.get(position).comprobante.comprobante.comprobante.comprobante.comprobante.caja_id
        holder.tvmetodoPago.text = info.get(position).metodoPago.nombreMetodoPago
        holder.tvtotal.text = info.get(position).comprobante.comprobante.comprobante.comprobante.comprobante.precioTotalPedido.toString()
        var context = holder.itemView.context

        holder.itemView.setOnClickListener{
            var intent = Intent(appConfig.CONTEXT, DetallesComprobante::class.java)
            intent.putExtra("comprobante",info[position])
            ContextCompat.startActivity(context, intent, null)
        }
    }
    fun actualizarComprobante(info:List<ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago>){
        this.info = info as ArrayList<ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago>
        notifyDataSetChanged()
    }
}