package com.example.project_kotlin.adaptador.adaptadores.categoria_plato

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.vistas.VistaItemCategoriaPlato
import com.example.project_kotlin.entidades.CategoriaPlato
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.categoria_platos.EditCatPlatoActivity

class CategoriaPlatoAdapter(var info : List<CategoriaPlato>)
    :RecyclerView.Adapter<VistaItemCategoriaPlato>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemCategoriaPlato {
        val vista = LayoutInflater.from(parent.context).
        inflate(R.layout.item_categoriaplato,parent,false)

        return VistaItemCategoriaPlato(vista)
    }

    override fun getItemCount(): Int = info.size

    override fun onBindViewHolder(holder: VistaItemCategoriaPlato, position: Int) {
        holder.tvCodCategoriaPlato.text = info.get(position).id
        holder.tvNombreCategoriaPlato.text = info.get(position).categoria
        var context = holder.itemView.context

        holder.itemView.setOnClickListener{

            var intent = Intent(appConfig.CONTEXT,EditCatPlatoActivity::class.java)
            intent.putExtra("categoriaPlato",info[position])
            ContextCompat.startActivity(context, intent, null)

        }

    }

    fun actualizarCategoria(info:List<CategoriaPlato>){

        this.info = info as ArrayList<CategoriaPlato>
        notifyDataSetChanged()

    }

}