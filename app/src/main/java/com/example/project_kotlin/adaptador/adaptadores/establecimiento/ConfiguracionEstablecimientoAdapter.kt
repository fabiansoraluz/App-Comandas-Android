package com.example.project_kotlin.adaptador.adaptadores.establecimiento

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.vistas.establecimiento.VistaItemAgregarEstablecimiento
import com.example.project_kotlin.entidades.Establecimiento
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.establecimiento.ActualizarEstablecimiento
import com.example.project_kotlin.vistas.mesas.ActualizarMesas

class ConfiguracionEstablecimientoAdapter (var info:List<Establecimiento>):RecyclerView.Adapter<VistaItemAgregarEstablecimiento>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemAgregarEstablecimiento {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_establecimineto, parent, false)
        return VistaItemAgregarEstablecimiento(vista)
    }

    override fun getItemCount(): Int=info.size


    override fun onBindViewHolder(holder: VistaItemAgregarEstablecimiento, position: Int) {


        holder.tvIdEstablecimiento.text=""+info.get(position).id
        holder.tvNombre.text=""+info.get(position).nomEstablecimiento
        holder.tvDireccion.text=""+info.get(position).direccionestablecimiento
        holder.tvRuc.text=""+info.get(position).rucestablecimiento
        holder.tvTelefono.text=""+info.get(position).telefonoestablecimiento


        var context = holder.itemView.context

        holder.itemView.setOnClickListener{
            var intent=Intent(appConfig.CONTEXT,ActualizarEstablecimiento::class.java)
            intent.putExtra("establecimiento",info[position])
            ContextCompat.startActivity(context, intent, null)

        }
    }



}