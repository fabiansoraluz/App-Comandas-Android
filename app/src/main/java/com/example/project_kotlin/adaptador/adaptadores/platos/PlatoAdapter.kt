package com.example.project_kotlin.adaptador.adaptadores.platos

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.vistas.platos.VistaItemPlato
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.project_kotlin.entidades.PlatoConCategoria
import com.example.project_kotlin.utils.appConfig

import com.example.project_kotlin.vistas.platos.ActualizarPlato

class PlatoAdapter(var info: List<PlatoConCategoria>) : RecyclerView.Adapter<VistaItemPlato>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemPlato {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_platos, parent, false)
        Log.d("Ingresar", "Plato")
        return VistaItemPlato(vista)
    }

    override fun getItemCount(): Int =  info.size


    override fun onBindViewHolder(holder: VistaItemPlato, position: Int) {


        holder.tvCodPlato.text = info.get(position).plato.id

        holder.tvNombrePlato.text = validarLongitudNombrePlato(info.get(position).plato.nombrePlato)

        holder.tvPrecioPlato.text = info.get(position).plato.precioPlato.toString()
        holder.tvCatNomPlato.text = info.get(position).categoriaPlato.categoria
        Glide.with(holder.itemView)
            .load(info.get(position).plato.nombreImagen)
            .placeholder(R.drawable.platos)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imagenPlato)


        var context = holder.itemView.context

        holder.itemView.setOnClickListener{
            var intent = Intent(appConfig.CONTEXT, ActualizarPlato::class.java)
            intent.putExtra("plato",info[position])
            ContextCompat.startActivity(context, intent, null)
        }

    }

    fun actualizarPlatos(info:List<PlatoConCategoria>){
        this.info = info
        notifyDataSetChanged()
    }
    fun validarLongitudNombrePlato(nombrePlato: String): String {
        val MAX_LONGITUD = 10
        val ACORTAR = 7

        return if (nombrePlato.length > MAX_LONGITUD) {
            nombrePlato.substring(0, ACORTAR) + "..."
        } else {
            nombrePlato
        }
    }
}