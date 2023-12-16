package com.example.project_kotlin.vistas.facturar

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.comprobantes.ComprobantesAdapter
import com.example.project_kotlin.adaptador.adaptadores.mesas.ConfiguracionMesasAdapter
import com.example.project_kotlin.dao.CajaDao
import com.example.project_kotlin.dao.ComprobanteDao
import com.example.project_kotlin.dao.MetodoPagoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago
import com.example.project_kotlin.entidades.EmpleadoUsuarioYCargo
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.IndexComandasActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DatosComprobantes : AppCompatActivity(){

    private lateinit var btnBuscar: Button
    private lateinit var btnVolver : Button

    private lateinit var spnCaja: Spinner
    private lateinit var edtFechaEmision: EditText
    private lateinit var edtPrecioInicial: EditText
    private lateinit var edtPrecioFin: EditText
    private lateinit var spnMetPago: Spinner
    private lateinit var tvEtiqueta : TextView
    private lateinit var listPagos:RecyclerView
    private lateinit var edtVentaTotal:EditText

    private lateinit var imgBorrarFecha : ImageButton
    //BASE DE DATOS
    private lateinit var comprobanteDao : ComprobanteDao
    private lateinit var cajaDao : CajaDao
    private lateinit var metodoPagoDao : MetodoPagoDao
    private lateinit var adaptador : ComprobantesAdapter
    private var decimalFormat = DecimalFormat("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.caja_activity)
        comprobanteDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comprobanteDao()
        btnBuscar = findViewById(R.id.btnBuscarComprobantes)
        btnVolver = findViewById(R.id.btnVolverListadoComprobantes)
        spnCaja = findViewById(R.id.spnCajaComprobantes)
        edtFechaEmision = findViewById(R.id.edtFechaEmisionComprobantes)
        edtPrecioInicial = findViewById(R.id.edtPrecioInicialComprobantes)
        edtPrecioFin = findViewById(R.id.edtPrecioFinComprobantes)
        spnMetPago = findViewById(R.id.spnMetPagoComprobantes)
        listPagos = findViewById(R.id.rvComprobantes)
        edtVentaTotal = findViewById(R.id.edtVentaTotalComprobantes)
        tvEtiqueta = findViewById(R.id.tvDatosComprobantes)
        btnVolver.setOnClickListener{volver()}
        cajaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cajaDao()
        metodoPagoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).metodoPagoDao()
        btnBuscar.setOnClickListener{filtrar()}
        imgBorrarFecha = findViewById(R.id.btnBorrarF)
        //AGREGAR LA IMAGEN
        imgBorrarFecha.setOnClickListener{
            edtFechaEmision.setText("")
        }
        edtFechaEmision.inputType = InputType.TYPE_NULL

        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this@DatosComprobantes,
            {
                    _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                edtFechaEmision.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        edtFechaEmision.setOnClickListener{
            datePickerDialog.show()
        }
        cargarCajas()
        cargarMetodosPago()
        cargarDatos()

    }
    fun filtrar(){
        val precioInicio = edtPrecioInicial.text.toString().toDoubleOrNull()
        val precioFinal = edtPrecioFin.text.toString().toDoubleOrNull()
        val metodoPagoId = spnMetPago.selectedItemPosition
        val numCaja = spnCaja.selectedItemPosition
        val fecha = edtFechaEmision.text
        lifecycleScope.launch(Dispatchers.IO){
            var datos = comprobanteDao.obtenerTodo()
            var datosFiltrados : List<ComprobanteComandaYEmpleadoYCajaYTipoComprobanteYMetodoPago> = datos
            if (precioInicio != null || precioFinal != null) {
                datosFiltrados = datosFiltrados.filter { comprobante ->
                    val precioTotal = comprobante.comprobante.comprobante.comprobante.comprobante.comprobante.precioTotalPedido
                    val cumpleRangoInicio = precioInicio == null || precioTotal >= precioInicio
                    val cumpleRangoFinal = precioFinal == null || precioTotal <= precioFinal
                    cumpleRangoInicio && cumpleRangoFinal
                }
            }
            if(metodoPagoId != 0){
                datosFiltrados = datosFiltrados.filter { comprobante -> comprobante.comprobante.comprobante.comprobante.comprobante.comprobante.metodopago_id == metodoPagoId  }

            }
            if(numCaja != 0){
                val idCaja = "C-00$numCaja"
                datosFiltrados = datosFiltrados.filter { comprobante -> comprobante.comprobante.comprobante.comprobante.comprobante.comprobante.caja_id.trim() == idCaja  }
            }
            if(!fecha.isNullOrEmpty()){
                datosFiltrados = datosFiltrados.filter { comprobante -> comprobante.comprobante.comprobante.comprobante.comprobante.comprobante.fechaEmision.trim().contains(fecha.trim())  }
            }
            val sumaPrecio = datosFiltrados.sumOf { it.comprobante.comprobante.comprobante.comprobante.comprobante.precioTotalPedido}
            withContext(Dispatchers.Main){
                adaptador.actualizarComprobante(datosFiltrados)
                edtVentaTotal.setText(decimalFormat.format(sumaPrecio))

            }
        }
    }
    fun cargarDatos(){
        lifecycleScope.launch(Dispatchers.IO){
            var datos = comprobanteDao.obtenerTodo()
            if(datos.size != 0)
                tvEtiqueta.visibility = View.GONE
            withContext(Dispatchers.Main) {
                adaptador = ComprobantesAdapter(datos)
                listPagos.layoutManager= LinearLayoutManager(this@DatosComprobantes)
                listPagos.adapter = adaptador
                val sumaPrecio = datos.sumOf { it.comprobante.comprobante.comprobante.comprobante.comprobante.precioTotalPedido}
                edtVentaTotal.setText(decimalFormat.format(sumaPrecio))
            }


        }
    }
    fun cargarCajas(){
        lifecycleScope.launch(Dispatchers.IO){
            var cajas = cajaDao.obtenerTodo()
            if(cajas.size > 0){
                val cajas = cajas.map { it.id }
                val opciones = mutableListOf<String>()
                opciones.add("Seleccionar caja")
                opciones.addAll(cajas)
                val adapter = ArrayAdapter(this@DatosComprobantes, android.R.layout.simple_spinner_item, opciones)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnCaja.adapter = adapter
            } else {
                val opcion = "No hay cajas"
                val adapter = ArrayAdapter(this@DatosComprobantes, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                spnCaja.adapter = adapter
                runOnUiThread {
                    btnBuscar.isEnabled = false
                }

            }
        }
    }
    fun cargarMetodosPago(){
        lifecycleScope.launch(Dispatchers.IO){
            var metodos = metodoPagoDao.buscarTodo()
            if(metodos.size > 0){
                val categorias = metodos.map { it.nombreMetodoPago }
                val opciones = mutableListOf<String>()
                opciones.add("Seleccionar método de pago")
                opciones.addAll(categorias)
                val adapter = ArrayAdapter(this@DatosComprobantes, android.R.layout.simple_spinner_item, opciones)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnMetPago.adapter = adapter
            } else {
                val opcion = "No hay métodos de pago"
                val adapter = ArrayAdapter(this@DatosComprobantes, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                spnMetPago.adapter = adapter
                runOnUiThread {
                    btnBuscar.isEnabled = false
                }

            }
        }
    }
    fun volver() {
        val intent = Intent(this, IndexComandasActivity::class.java)
        startActivity(intent)
    }

}