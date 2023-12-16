package com.example.project_kotlin.service

import com.example.project_kotlin.entidades.Caja
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceCaja {
    @POST("/configuracion/caja/registrar")
    fun fetchGuardarCaja(@Body bean: Caja): Call<Void>

    @PUT("/configuracion/caja/actualizar")
    fun fetchActualizarCaja(@Body bean: Caja): Call<Void>

    @DELETE("/configuracion/caja/eliminar/{codigo}")
    fun fetcEliminarCaja(@Path("codigo") codigo: String): Call<Void>
}