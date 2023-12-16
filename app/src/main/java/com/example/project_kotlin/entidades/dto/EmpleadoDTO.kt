package com.example.project_kotlin.entidades.dto

import com.example.project_kotlin.entidades.Cargo
import com.example.project_kotlin.entidades.Usuario

class EmpleadoDTO(var id: Long = 0,
                  var nombre : String,
                  var apellido : String,
                  var telefono : String,
                  var dni : String,
                  var fechaRegistro: String = "",
                  var usuario: Usuario,
                  var cargo:Cargo):java.io.Serializable {
}