package com.example.project_kotlin.service

import com.example.project_kotlin.entidades.Establecimiento
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceEstablecimiento {

    @POST("/configuracion/establecimiento/grabar")
    fun fetchGuardarEstablecimiento(@Body bean: Establecimiento): Call<Void>

    @PUT("/configuracion/establecimiento/actualizar")
    fun fetchUpdateEstablecimiento(@Body bean: Establecimiento): Call<Void>

    @DELETE("/configuracion/establecimiento/eliminar/{codigo}")
    fun fetcEliminarEstablecimiento(@Path("codigo") codigo: Int): Call<Void>

}