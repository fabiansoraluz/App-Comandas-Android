package com.example.project_kotlin.entidades.firebase

import com.example.project_kotlin.entidades.dto.ComandaDTO
import com.example.project_kotlin.entidades.dto.PlatoDTO

class DetalleComandaNoSql(
    var cantidadPedido : Int,
    var precioUnitario : Double,
    var observacion : String,
    var plato : PlatoNoSql
) {
}