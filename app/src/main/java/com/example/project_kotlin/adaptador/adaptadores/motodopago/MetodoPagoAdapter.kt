package com.example.project_kotlin.adaptador.adaptadores.motodopago

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.project_kotlin.R
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.adaptador.vistas.metodopago.VistaItemMetodoPago
import com.example.project_kotlin.entidades.MetodoPago
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.metodo_pago.ActualizarMetodoPago

class MetodoPagoAdapter(var info:List<MetodoPago>): RecyclerView.Adapter<VistaItemMetodoPago>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemMetodoPago {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_metodo_pago, parent, false)
        return VistaItemMetodoPago(vista)
    }

    override fun getItemCount(): Int = info.size

    override fun onBindViewHolder(holder: VistaItemMetodoPago, position: Int) {

        holder.tvMetPago.text = "" + info.get(position).nombreMetodoPago
        var context = holder.itemView.context

        holder.itemView.setOnClickListener {
            var intent = Intent(appConfig.CONTEXT, ActualizarMetodoPago::class.java)
            //mandar un objeto = MetodoPago
            intent.putExtra("metodo_pago", info[position])
            ContextCompat.startActivity(context, intent, null)
        }
    }

    fun ActualizarMetodoPago(info:List<MetodoPago>){
        this.info = info
        notifyDataSetChanged()
    }
}