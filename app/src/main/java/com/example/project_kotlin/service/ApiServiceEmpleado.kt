package com.example.project_kotlin.service

import com.example.project_kotlin.entidades.Empleado
import com.example.project_kotlin.entidades.Mesa
import com.example.project_kotlin.entidades.Usuario
import com.example.project_kotlin.entidades.dto.EmpleadoDTO
import retrofit2.http.Body
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiServiceEmpleado {
    @POST("/configuracion/empleado/registrar")
    fun fetchGuardarEmpleado(@Body bean:EmpleadoDTO):Call<Void>

    @PUT("/configuracion/empleado/actualizar")
    fun fetchActualizarEmpleado(@Body bean: EmpleadoDTO): Call<Void>

    @DELETE("/configuracion/empleado/eliminar/{codigo}")
    fun fetcEliminarEmpleado(@Path("codigo") codigo: Int): Call<Void>
}