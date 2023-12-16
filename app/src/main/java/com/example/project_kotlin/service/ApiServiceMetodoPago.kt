package com.example.project_kotlin.service

import com.example.project_kotlin.entidades.MetodoPago
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceMetodoPago {

    @POST("/configuracion/metodo-pago/registrar")
    fun fetchGuardarMetodoPago(@Body bean: MetodoPago): Call<Void>

    @PUT("/configuracion/metodo-pago/actualizar")
    fun fetchActualizarMetodoPago(@Body bean: MetodoPago): Call<Void>

    @DELETE("/configuracion/metodo-pago/eliminar/{codigo}")
    fun fetcEliminarMetodoPago(@Path("codigo") codigo: Long): Call<Void>

}