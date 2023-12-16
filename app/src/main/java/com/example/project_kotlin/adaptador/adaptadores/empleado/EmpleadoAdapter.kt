package com.example.project_kotlin.adaptador.adaptadores.empleado

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.vistas.empleados.VistaItemUsuario
import com.example.project_kotlin.entidades.EmpleadoUsuarioYCargo
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.empleados.ActualizarEmpleado

class EmpleadoAdapter(var info:List<EmpleadoUsuarioYCargo>):RecyclerView.Adapter<VistaItemUsuario>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaItemUsuario {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        Log.d("Ingresar", "Empleado")
        Log.d("TAMAÑO", " " + info.size)
        return VistaItemUsuario(vista)
    }

    override fun getItemCount(): Int = info.size

    override fun onBindViewHolder(holder: VistaItemUsuario, position: Int) {
        holder.tvApeUsu.text =  info.get(position).empleado.empleado.apellidoEmpleado
        holder.tvDniUsu.text = info.get(position).empleado.empleado.dniEmpleado
        holder.tvNomUsu.text = info.get(position).empleado.empleado.nombreEmpleado
        holder.tvCorreoUsu.text = info.get(position).usuario.correo
        holder.tvCargoUsu.text = info.get(position).empleado.cargo.cargo
        holder.tvTelfUsu.text = info.get(position).empleado.empleado.telefonoEmpleado
        holder.tvFechaUsu.text = info.get(position).empleado.empleado.fechaRegistro
        var context = holder.itemView.context

        holder.itemView.setOnClickListener{
            var intent = Intent(appConfig.CONTEXT,ActualizarEmpleado::class.java )
            intent.putExtra("empleado", info[position])
            ContextCompat.startActivity(context, intent, null)

        }
    }
    fun actualizarListaEmpleados(info:List<EmpleadoUsuarioYCargo>){
        this.info = info
        notifyDataSetChanged()
    }
}