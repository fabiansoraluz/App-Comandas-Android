package com.example.project_kotlin.entidades

import androidx.room.*

@Entity(tableName = "comanda",
    foreignKeys = [
        ForeignKey(
        entity = Mesa::class,
        parentColumns = ["id"],
        childColumns = ["mesa_id"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = EstadoComanda::class,
        parentColumns = ["id"],
        childColumns = ["estado_comanda_id"],
        onDelete = ForeignKey.CASCADE)
        , ForeignKey(
        entity = Empleado::class,
        parentColumns = ["id"],
        childColumns = ["empleado_id"],
        onDelete = ForeignKey.CASCADE)],
    indices = [
        Index("mesa_id"),
        Index("estado_comanda_id"),
        Index("empleado_id"),
    ])
 class Comanda (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "cantidadAsientos") var cantidadAsientos: Int,
    @ColumnInfo(name = "precioTotal") var precioTotal: Double = 0.0,
    @ColumnInfo(name="fechaEmision",defaultValue = "CURRENT_TIMESTAMP")var fechaRegistro: String = "",
    @ColumnInfo(name = "mesa_id") var mesaId: Int,
    @ColumnInfo(name = "estado_comanda_id") var estadoComandaId: Int,
    @ColumnInfo(name = "empleado_id") var empleadoId: Int
):java.io.Serializable {
}

data class ComandaMesa(
    @Embedded val comanda:Comanda,
    @Relation(
        parentColumn="mesa_id",
        entityColumn="id"
    )
    val mesa:Mesa
):java.io.Serializable

data class ComandaMesaYEmpleado(
    @Embedded val comanda:ComandaMesa,
    @Relation(
        parentColumn="empleado_id",
        entityColumn="id"
    )
    val empleado:Empleado
):java.io.Serializable

data class ComandaMesaYEmpleadoYEstadoComanda(
    @Embedded val comanda:ComandaMesaYEmpleado,
    @Relation(
        parentColumn="estado_comanda_id",
        entityColumn="id"
    )
    val estadoComanda:EstadoComanda
):java.io.Serializable

