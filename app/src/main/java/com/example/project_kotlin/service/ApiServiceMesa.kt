package com.example.project_kotlin.service

import retrofit2.Call
import com.example.project_kotlin.entidades.Mesa
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceMesa {
    @POST("/configuracion/mesa/grabar")
    fun fetchGuardarMesa(@Body bean:Mesa): Call<Void>

    @PUT("/configuracion/mesa/actualizar")
    fun fetchActualizarMesa(@Body bean:Mesa): Call<Void>

    @DELETE("/configuracion/mesa/eliminar/{codigo}")
    fun fetcEliminarMesa(@Path("codigo") codigo: Int): Call<Void>
}