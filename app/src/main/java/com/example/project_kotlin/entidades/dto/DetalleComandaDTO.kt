package com.example.project_kotlin.entidades.dto

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.project_kotlin.entidades.Comanda
import org.jetbrains.annotations.NotNull

class DetalleComandaDTO(var id : Int = 0,
                        var cantidadPedido : Int,
                        var precioUnitario : Double,
                        var observacion : String,
                        var comanda : ComandaDTO? = null,
                        var plato : PlatoDTO):java.io.Serializable   {
}