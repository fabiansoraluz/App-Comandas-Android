package com.example.project_kotlin.vistas.facturar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.project_kotlin.R
import com.example.project_kotlin.entidades.Comanda
import com.example.project_kotlin.entidades.ComandaMesaYEmpleadoYEstadoComanda
import com.example.project_kotlin.entidades.Comprobante
import com.example.project_kotlin.entidades.ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago
import com.example.project_kotlin.vistas.comandas.EditarComanda


class DetallesComprobante : AppCompatActivity() {
    private lateinit var comprobanteGlobal: ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago
    private lateinit var edIdComprobante : TextView
    private lateinit var edCliente : TextView
    private lateinit var edFecha : TextView
    private lateinit var edTotalPedido : TextView
    private lateinit var edCaja : TextView
    private lateinit var edTipoComprobante : TextView
    private lateinit var edEmpleado : TextView
    private lateinit var edComandaID : TextView
    private lateinit var edMetodoPago : TextView
    private lateinit var edIGV : TextView
    private lateinit var edSubTotal : TextView
    private lateinit var edDescuento : TextView
    private lateinit var btnVolver: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.comprobantepago)
        comprobanteGlobal = intent.getSerializableExtra("comprobante") as ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago
        edIdComprobante = findViewById(R.id.rvIdComprobante)
        edCliente = findViewById(R.id.rvNombreCliente)
        edFecha = findViewById(R.id.rvFecheEmision)
        edTotalPedido = findViewById(R.id.rvPrecioTotalPedido)
        edCaja= findViewById(R.id.rvCajaId)
        edTipoComprobante = findViewById(R.id.rvTipoComprobante)
        edEmpleado = findViewById(R.id.rvEmpleado)
        edComandaID = findViewById(R.id.rvComandaId)
        edMetodoPago = findViewById(R.id.rvMetodoPago)
        edIGV = findViewById(R.id.rvIgv)
        edSubTotal = findViewById(R.id.rvSubTotal)
        edDescuento = findViewById(R.id.rvDescuento)
        btnVolver = findViewById(R.id.btnVolverListadoComprobantes)
        btnVolver.setOnClickListener{volver()}
        cargarDatos()
    }
    fun cargarDatos(){
        edIdComprobante.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.id.toString())
        edCliente.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.nombreCliente)
        edFecha.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.fechaEmision)
        edTotalPedido.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.precioTotalPedido.toString())
        edCaja.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.caja_id)
        edTipoComprobante.setText(comprobanteGlobal.comprobante.tipoComprobante.tipo)
        edEmpleado.setText(comprobanteGlobal.comprobante.comprobante.comprobante.empleado.nombreEmpleado)
        edComandaID.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.comanda_id.toString())
        edMetodoPago.setText(comprobanteGlobal.metodoPago.nombreMetodoPago)
        edIGV.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.igv.toString())
        edSubTotal.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.subTotal.toString())
        edDescuento.setText(comprobanteGlobal.comprobante.comprobante.comprobante.comprobante.comprobante.descuento.toString())
    }
    fun volver(){
        val intent = Intent(this, DatosComprobantes::class.java)
        startActivity(intent)
        finish()
    }
}