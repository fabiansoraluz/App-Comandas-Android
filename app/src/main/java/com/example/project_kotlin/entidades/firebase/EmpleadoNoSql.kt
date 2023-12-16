package com.example.project_kotlin.entidades.firebase

class EmpleadoNoSql(var nombre : String,
                    var apellido : String,
                    var telefono : String,
                    var dni : String,
                    var fechaRegistro: String = "",
                    var usuario: UsuarioNoSql,
                    var cargo: CargoNoSql)