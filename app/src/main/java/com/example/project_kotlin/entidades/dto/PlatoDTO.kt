package com.example.project_kotlin.entidades.dto

import com.example.project_kotlin.entidades.CategoriaPlato

class PlatoDTO (var id: String  = "",
                var nombre : String,
                var imagen :    String,
                var precioPlato: Double,
                var categoriaPlato : CategoriaPlato
                ):java.io.Serializable{
                }