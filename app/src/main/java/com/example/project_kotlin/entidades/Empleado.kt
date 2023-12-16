package com.example.project_kotlin.entidades

import androidx.annotation.NonNull
import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "Empleado",
    foreignKeys = [
        ForeignKey(
            entity = Cargo::class,
            parentColumns = ["id"],
            childColumns = ["cargo_id"]
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuario_id"]
        )
    ],
    indices = [
        Index("usuario_id"),
        Index("cargo_id")
    ])
class Empleado(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @NonNull @ColumnInfo(name="nombre") var nombreEmpleado : String,
    @NonNull @ColumnInfo(name="apellido") var apellidoEmpleado : String,
    @NonNull @ColumnInfo(name="telefono") var telefonoEmpleado : String,
    @NonNull @ColumnInfo(name="dni") var dniEmpleado : String,
    @ColumnInfo(name = "fecha_registro", defaultValue = "CURRENT_TIMESTAMP") var fechaRegistro: String = "",
    @ColumnInfo(name="cargo_id") var cargo_id : Int,
    @ColumnInfo(name="usuario_id") var usuario_id : Int,

    ):java.io.Serializable {

}

data class EmpleadoCargo(
    @Embedded val empleado: Empleado,
    @Relation(
        parentColumn = "cargo_id",
        entityColumn = "id"
    )
    val cargo: Cargo
):java.io.Serializable

data class EmpleadoUsuarioYCargo(
    @Embedded val empleado: EmpleadoCargo,
    @Relation(
        parentColumn = "usuario_id",
        entityColumn = "id"
    )
    val usuario: Usuario
):java.io.Serializable
