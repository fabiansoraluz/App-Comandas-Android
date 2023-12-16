package com.example.project_kotlin.adaptador.adaptadores.cajas

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.vistas.cajas.VistaItemCajasExistentes

import com.example.project_kotlin.entidades.Caja
import com.example.project_kotlin.vistas.caja_registradora.ActualizarCajas

class ConfiguracionCajasAdapter(private var info: List<Caja>) : RecyclerView.Adapter<VistaItemCajasExistentes>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemCajasExistentes {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_cajas_existentes, parent, false)
        return VistaItemCajasExistentes(vista)
    }

    override fun getItemCount(): Int = info.size

    override fun onBindViewHolder(holder: VistaItemCajasExistentes, position: Int) {
        val caja = info[position]
        val cajaId = caja.id.toString()
        val cajaEstablecimiento = caja.establecimiento?.nomEstablecimiento.toString()

        holder.tvIdCaja.text = cajaId
        holder.tvNombEstablecimiento.text = cajaEstablecimiento


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ActualizarCajas::class.java)
            intent.putExtra("caja", caja)
            holder.itemView.context.startActivity(intent)
        }
    }

    fun actualizarListaCajas(info: List<Caja>) {
        this.info = info
        notifyDataSetChanged()
    }
}
