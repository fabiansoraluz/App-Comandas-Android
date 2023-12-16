package com.example.project_kotlin.entidades

import androidx.room.*
import java.util.Date

@Entity(tableName = "comprobante",
    foreignKeys = [
        ForeignKey(
            entity = Comanda::class,
            parentColumns = ["id"],
            childColumns = ["comanda_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Caja::class,
            parentColumns = ["id"],
            childColumns = ["caja_id"],
            onDelete = ForeignKey.CASCADE
        ),ForeignKey(
        entity = TipoComprobante::class,
        parentColumns = ["id"],
        childColumns = ["tipocomprobante_id"],
        onDelete = ForeignKey.CASCADE)
        , ForeignKey(
        entity = Empleado::class,
        parentColumns = ["id"],
        childColumns = ["empleado_id"],
        onDelete = ForeignKey.CASCADE),
        ForeignKey(
            entity = MetodoPago::class,
            parentColumns = ["id"],
            childColumns = ["metodopago_id"],
            onDelete = ForeignKey.CASCADE
        )],
    indices = [
        Index("comanda_id"),
        Index("caja_id"),
        Index("tipocomprobante_id"),
        Index("empleado_id"),
        Index("metodopago_id")
    ])
data class Comprobante (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "nombreCliente") var nombreCliente:String,
    @ColumnInfo(name = "fechaEmision", defaultValue = "CURRENT_TIMESTAMP") var fechaEmision: String = "",
    @ColumnInfo(name= " igv")var igv:Double=0.00,
    @ColumnInfo(name= " subTotal")var subTotal:Double=0.00,
    @ColumnInfo(name= " descuento")var descuento:Double=0.00,
    @ColumnInfo(name = "precioTotalPedido") var precioTotalPedido: Double = 0.0,
    @ColumnInfo(name="comanda_id") var comanda_id : Int,
    @ColumnInfo(name="caja_id") var caja_id : String,
    @ColumnInfo(name="tipocomprobante_id") var tipocomprobante_id : Int,
    @ColumnInfo(name="empleado_id") var empleado_id : Int,
    @ColumnInfo(name="metodopago_id") var metodopago_id : Int,
    ):java.io.Serializable{
        
}
data class ComprobanteComanda(
    @Embedded val comprobante:Comprobante,
    @Relation(
        parentColumn="comanda_id",
        entityColumn="id"
    )
    val comanda:Comanda
):java.io.Serializable

data class ComprobanteComandaYEmpleado(
    @Embedded val comprobante:ComprobanteComanda,
    @Relation(
        parentColumn="empleado_id",
        entityColumn="id"
    )
    val empleado:Empleado
):java.io.Serializable

data class ComprobanteComandaYEmpleadoYCaja(
    @Embedded val comprobante:ComprobanteComandaYEmpleado,
    @Relation(
        parentColumn="caja_id",
        entityColumn="id"
    )
    val caja:Caja
):java.io.Serializable
data class ComprobanteComandaYEmpleadoYCajaYTipoComprobante(
    @Embedded val comprobante:ComprobanteComandaYEmpleadoYCaja,
    @Relation(
        parentColumn="tipocomprobante_id",
        entityColumn="id"
    )
    val tipoComprobante:TipoComprobante
):java.io.Serializable

data class ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago(
    @Embedded val comprobante:ComprobanteComandaYEmpleadoYCajaYTipoComprobante,
    @Relation(
        parentColumn="metodopago_id",
        entityColumn="id"
    )
    val metodoPago:MetodoPago
):java.io.Serializable
