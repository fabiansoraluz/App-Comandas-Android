package com.example.project_kotlin.entidades.firebase

import com.example.project_kotlin.entidades.Caja
import com.example.project_kotlin.entidades.MetodoPago
import com.example.project_kotlin.entidades.TipoComprobante
import com.example.project_kotlin.entidades.dto.ComandaDTO
import com.example.project_kotlin.entidades.dto.EmpleadoDTO

class ComprobanteNoSql(var fechaEmision:String ="", var precioTotalPedido:Double=0.00, var igv:Double=0.00, var subTotal:Double=0.00,
                       var descuento:Double=0.00, var nombreCliente:String, var comanda: ComandaNoSql, var empleado: EmpleadoNoSql, var caja: CajaNoSql, var metodoPago: MetodoPagoNoSql,
                       var tipoComprobante: TipoComprobanteNoSql
):java.io.Serializable {
}