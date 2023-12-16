package com.example.project_kotlin.adaptador.adaptadores.mesas

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.vistas.mesas.VistaItemAgregarMesa
import com.example.project_kotlin.entidades.Mesa
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.mesas.ActualizarMesas

class ConfiguracionMesasAdapter(var info:List<Mesa>):RecyclerView.Adapter<VistaItemAgregarMesa>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemAgregarMesa {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_agregar_mesa, parent, false)
        return VistaItemAgregarMesa(vista)
    }

    override fun getItemCount(): Int = info.size


    override fun onBindViewHolder(holder: VistaItemAgregarMesa, position: Int) {
        holder.tvID.text = "" + info.get(position).id
        holder.tvCanAsientos.text = "" + info.get(position).cantidadAsientos
        holder.tvEstadoMesa.text = "" + info.get(position).estado
        var context = holder.itemView.context

        holder.itemView.setOnClickListener{

            var intent = Intent(appConfig.CONTEXT, ActualizarMesas::class.java)
            intent.putExtra("mesa", info[position])
            ContextCompat.startActivity(context, intent, null)
        }

    }
    fun actualizarListaMesas(info:List<Mesa>){
        this.info = info
        notifyDataSetChanged()
    }
}