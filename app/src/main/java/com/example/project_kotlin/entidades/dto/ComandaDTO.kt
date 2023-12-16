package com.example.project_kotlin.entidades.dto

import com.example.project_kotlin.entidades.Empleado
import com.example.project_kotlin.entidades.EstadoComanda
import com.example.project_kotlin.entidades.Mesa

class ComandaDTO (
    var id:Long=0, var cantidadAsientos:Int,var precioTotal:Double=0.00, var fechaEmision:String="",
    var mesa:Mesa,var estadoComanda: EstadoComanda, var empleado: EmpleadoDTO, var listaDetalleComanda : List<DetalleComandaDTO>? = mutableListOf(),
    var comprobante : ComprobanteDTO? = null
):java.io.Serializable {

}