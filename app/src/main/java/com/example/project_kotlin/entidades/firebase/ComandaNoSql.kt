package com.example.project_kotlin.entidades.firebase

import com.example.project_kotlin.entidades.EstadoComanda
import com.example.project_kotlin.entidades.Mesa
import com.example.project_kotlin.entidades.dto.ComprobanteDTO
import com.example.project_kotlin.entidades.dto.DetalleComandaDTO
import com.example.project_kotlin.entidades.dto.EmpleadoDTO

class ComandaNoSql(var cantidadAsientos:Int, var precioTotal:Double=0.00, var fechaEmision:String="",
                   var mesa: MesaNoSql, var estadoComanda: EstadoComandaNoSql, var empleado: EmpleadoNoSql, var listaDetalleComanda : List<DetalleComandaNoSql>? = mutableListOf(),
                   var comprobante : ComprobanteDTO? = null) {
}