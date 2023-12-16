package com.example.project_kotlin.service

import com.example.project_kotlin.entidades.CategoriaPlato
import com.example.project_kotlin.entidades.dto.CategoriaPlatoDTO
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceCategoriaPlato {

    @POST("/configuracion/categoriaPlato/registrar")
    fun fetchGuardarCategoria(@Body bean: CategoriaPlatoDTO): Call<Void>

    @PUT("/configuracion/categoriaPlato/actualizar")
    fun fetchActualizarCategoria(@Body bean: CategoriaPlatoDTO): Call<Void>

    @DELETE("/configuracion/categoriaPlato/eliminar/{codigo}")
    fun fetcEliminarCategoria(@Path("codigo") codigo: String): Call<Void>

}