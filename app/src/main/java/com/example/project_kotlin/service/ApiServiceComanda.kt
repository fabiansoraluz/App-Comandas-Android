package com.example.project_kotlin.service

import com.example.project_kotlin.entidades.dto.ComandaDTO
import com.example.project_kotlin.entidades.dto.EmpleadoDTO
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceComanda {
    @POST("/configuracion/comanda/registrar")
    fun fetchGuardarComanda(@Body bean: ComandaDTO): Call<Void>

    @PUT("/configuracion/comanda/actualizar/{codigo}")
    fun fetchActualizarComandayDetalleComanda(@Path("codigo") codigo: Int, @Body bean:ComandaDTO): Call<Void>

    @DELETE("/configuracion/comanda/eliminar/{codigo}")
    fun fectDeleteComandayDetalleComanda(@Path("codigo") codigo:Int):Call<Void>
}