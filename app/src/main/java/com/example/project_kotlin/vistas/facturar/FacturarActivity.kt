package com.example.project_kotlin.vistas.facturar

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.*
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.*
import com.example.project_kotlin.entidades.dto.*
import com.example.project_kotlin.entidades.firebase.*
import com.example.project_kotlin.service.ApiServiceComanda
import com.example.project_kotlin.service.ApiServiceComprobante
import com.example.project_kotlin.service.ApiServiceMesa
import com.example.project_kotlin.utils.ApiUtils
import com.example.project_kotlin.utils.VariablesGlobales
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.comandas.ComandasVista
import com.example.project_kotlin.vistas.comandas.EditarComanda
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FacturarActivity: AppCompatActivity() {
    //BOTONES
    private lateinit var btnFacturar : Button
    private lateinit var btnVolver : Button
    //Inputs
    private lateinit var spnMetodoPago : Spinner
    private lateinit var spnTipoComprobante : Spinner
    private lateinit var spnCaja : Spinner
    private lateinit var edIdComanda : EditText
    private lateinit var edEmpleado : EditText
    private lateinit var edCliente : EditText
    private lateinit var edIGV : EditText
    private lateinit var edSubTotal : EditText
    private lateinit var edDescuento : EditText
    private lateinit var edTotalPagar : EditText
    //BD
    private lateinit var categoriaDao : CategoriaPlatoDao
    private lateinit var detalleComandaDao : DetalleComandaDao
    private lateinit var comprobanteDao : ComprobanteDao
    private lateinit var apiComprobante : ApiServiceComprobante
    private lateinit var mesaDao: MesaDao
    private lateinit var empleadoDao : EmpleadoDao
    private lateinit var apiMesa : ApiServiceMesa
    private lateinit var comandaDao: ComandaDao
    private lateinit var apiComanda : ApiServiceComanda
    private lateinit var cajaDao : CajaDao
    private lateinit var metodoPagoDao : MetodoPagoDao
    private lateinit var tipoComprobanteDao : TipoComprobanteDao
    private lateinit var bdFirebase : DatabaseReference
    //ComandaGlobal
    //Llamando al objeto comandas con mesa, empleados y estadocomanda
    private lateinit var comandabean: ComandaMesaYEmpleadoYEstadoComanda
    private  var detalleComandaGlobal : List<DetalleComandaConPlato> = emptyList()
    //GLOBALES
    private lateinit var EmpleadoGlobal : EmpleadoUsuarioYCargo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.facturar_activity)
        //Inicializar componentes
        if(VariablesGlobales.empleado != null){
            EmpleadoGlobal = VariablesGlobales.empleado!!
        }
        btnFacturar = findViewById(R.id.BtnFacturarF)
        btnVolver = findViewById(R.id.BtnCancelar)
        spnMetodoPago = findViewById(R.id.spnMetPagoF)
        spnTipoComprobante = findViewById(R.id.spnTipoComprobante)
        spnCaja = findViewById(R.id.spnCajasP)
        edIdComanda = findViewById(R.id.edtIdComanda)
        edEmpleado = findViewById(R.id.edtEmpleadoNombreFacturar)
        edCliente = findViewById(R.id.edtClienteF)
        edIGV = findViewById(R.id.edtIGV)
        edSubTotal = findViewById(R.id.edtSubTotal)
        edDescuento = findViewById(R.id.edtDescuento)
        edTotalPagar = findViewById(R.id.edtPrecioTotal)
        edDescuento.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        //Inicializar room
        empleadoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).empleadoDao()
        comprobanteDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comprobanteDao()
        mesaDao =ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).mesaDao()
        apiMesa = ApiUtils.getAPIServiceMesa()
        comandaDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comandaDao()
        apiComanda= ApiUtils.getApiServiceComanda()
        cajaDao =ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cajaDao()
        metodoPagoDao =ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).metodoPagoDao()
        tipoComprobanteDao =ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).tipoComprobanteDao()
        detalleComandaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).detalleComandaDao()
        apiComprobante = ApiUtils.getApiServiceComprobante()
        categoriaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        conectar()
        //Comanda global
        comandabean = intent.getSerializableExtra("comandaFacturar") as ComandaMesaYEmpleadoYEstadoComanda
        lifecycleScope.launch(Dispatchers.IO) {
            detalleComandaGlobal = detalleComandaDao.buscarDetallesPorComanda(comandabean.comanda.comanda.comanda.id)
        }
        //EVENTOS
        btnVolver.setOnClickListener{volver()}
        btnFacturar.setOnClickListener{facturar()}
    }
    override fun onStart() {
        super.onStart()
        cargarDatos()
    }
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }
    private fun cargarDatos(){
        //DATOS
        edIdComanda.setText(comandabean.comanda.comanda.comanda.id.toString())
        edEmpleado.setText(VariablesGlobales.empleado?.empleado?.empleado?.nombreEmpleado)
        //CALCULAR
        val sumaPrecio = detalleComandaGlobal.sumOf { it.detalle.precioUnitario }
        edSubTotal.setText(sumaPrecio.toString())
        val igv = sumaPrecio * 0.18
        edIGV.setText(igv.toString())
        val totalPagar = sumaPrecio + igv
        edTotalPagar.setText(totalPagar.toString())
        cargarCajas()
        cargarMetodosPago()
        cargarTiposComprobante()
    }
    fun cargarMetodosPago(){
        lifecycleScope.launch(Dispatchers.IO){
            var metodos = metodoPagoDao.buscarTodo()
            if(metodos.size > 0){
                val categorias = metodos.map { it.nombreMetodoPago }
                val adapter = ArrayAdapter(this@FacturarActivity, android.R.layout.simple_spinner_item, categorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnMetodoPago.adapter = adapter
            } else {
                val opcion = "No hay métodos de pago"
                val adapter = ArrayAdapter(this@FacturarActivity, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                spnMetodoPago.adapter = adapter
                runOnUiThread {
                    btnFacturar.isEnabled = false
                }

            }
        }
    }
    fun cargarCajas(){
        lifecycleScope.launch(Dispatchers.IO){
            var cajas = cajaDao.obtenerTodo()
            if(cajas.size > 0){
                val categorias = cajas.map { it.id }
                val adapter = ArrayAdapter(this@FacturarActivity, android.R.layout.simple_spinner_item, categorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnCaja.adapter = adapter
            } else {
                val opcion = "No hay cajas"
                val adapter = ArrayAdapter(this@FacturarActivity, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                spnCaja.adapter = adapter
                runOnUiThread {
                    btnFacturar.isEnabled = false
                }

            }
        }
    }
    fun cargarTiposComprobante(){
        lifecycleScope.launch(Dispatchers.IO){
            var tiposComprobante = tipoComprobanteDao.obtenerTodo()
            val categorias = tiposComprobante.map { it.tipo }
            val adapter = ArrayAdapter(this@FacturarActivity, android.R.layout.simple_spinner_item, categorias)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnTipoComprobante.adapter = adapter
        }
    }
    override fun onBackPressed() {
        volver()
    }
    fun facturar(){
        val cajaID = spnCaja.selectedItemPosition+1
        var empleadoID : Int = 0
        val comandaId = edIdComanda.text.toString()
        val tipoComprobanteId = spnCaja.selectedItemPosition+1
        val metodoPagoId = spnMetodoPago.selectedItemPosition+1
        var clienteNombre = edCliente.text.toString()
        val igv = edIGV.text.toString().toDouble()
        val subTOTAL = edSubTotal.text.toString().toDouble()
        var descuento = edDescuento.text.toString().toDoubleOrNull()
        var totalPagar = edTotalPagar.text.toString().toDouble()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val fechaActual = Date()
        val fechaFormateada = dateFormat.format(fechaActual)
        if(VariablesGlobales.empleado?.empleado?.empleado?.id != null){
            empleadoID = VariablesGlobales.empleado?.empleado?.empleado?.id!!.toInt()
        }

        if(clienteNombre.length == 0) clienteNombre = "Cliente"
        lifecycleScope.launch(Dispatchers.IO){
            if(descuento == null) descuento = 0.0
            if(descuento!! > totalPagar){
                mostrarToast("El descuento no puede ser mayor al precio total")
                return@launch
            }
            val metodoPago = metodoPagoDao.buscarPorId(metodoPagoId.toLong())
            val tipoComprobante = tipoComprobanteDao.obtenerPorId(tipoComprobanteId.toLong())
            totalPagar = totalPagar - descuento!!
            val cajaIdFormateada = "C-00$cajaID"
            val caja = cajaDao.obtenerPorId(cajaIdFormateada)
            val comprobanteRoom = Comprobante(nombreCliente = clienteNombre, fechaEmision = fechaFormateada,
            igv = igv, subTotal = subTOTAL, descuento = descuento!!, caja_id = cajaIdFormateada, precioTotalPedido = totalPagar,
            comanda_id = comandaId.toInt(), metodopago_id = metodoPagoId, tipocomprobante_id = tipoComprobanteId,
            empleado_id = empleadoID)

            val idCDP = comprobanteDao.guardar(comprobanteRoom)
            comandabean.comanda.comanda.mesa.estado = "Libre"
            comandabean.comanda.comanda.comanda.estadoComandaId = 2
            mesaDao.actualizar(comandabean.comanda.comanda.mesa)
            comandaDao.actualizar(comandabean.comanda.comanda.comanda)

            //ACTUALIZAR MESA
            comandabean.comanda.comanda.mesa.estado = "Libre"
            actualizarMesaMysql(comandabean.comanda.comanda.mesa)
            bdFirebase.child("mesa").child(comandabean.comanda.comanda.mesa.id.toString()).setValue(MesaNoSql(comandabean.comanda.comanda.mesa.cantidadAsientos,
                comandabean.comanda.comanda.mesa.estado))
            //ACTUALIZAR COMANDA
            val detalleComandaDTOS : MutableList<DetalleComandaDTO> =  mutableListOf()
            val detalleComandaNoSql : MutableList<DetalleComandaNoSql> =  mutableListOf()
            detalleComandaGlobal.forEach{detalleComanda ->
                val categoriaPlato = categoriaDao.obtenerPorId(detalleComanda.plato.catplato_id)
                //GUARDAR PARA MYSQL
                val platoDTO = PlatoDTO(detalleComanda.plato.id, detalleComanda.plato.nombrePlato, detalleComanda.plato.nombreImagen,
                    detalleComanda.plato.precioPlato, CategoriaPlato(id = detalleComanda.plato.catplato_id, categoria = categoriaPlato.categoria))
                detalleComandaDTOS.add(
                    DetalleComandaDTO(id = 0, cantidadPedido = detalleComanda.detalle.cantidadPedido,
                        precioUnitario = detalleComanda.detalle.precioUnitario, observacion = detalleComanda.detalle.observacion,
                        plato = platoDTO)
                )
                //GUARDAR PARA FIREBASE
                detalleComandaNoSql.add(
                    DetalleComandaNoSql(detalleComanda.detalle.cantidadPedido, detalleComanda.detalle.precioUnitario,
                        detalleComanda.detalle.observacion, PlatoNoSql(platoDTO.nombre,platoDTO.imagen, platoDTO.precioPlato, CategoriaPlatoNoSql(categoria = categoriaPlato.categoria))
                    )
                )
            }
            val comandaRecibida = comandabean.comanda.comanda.comanda
            val empleado = empleadoDao.obtenerPorId(comandaRecibida.empleadoId.toLong())
            val empleadoDTO = EmpleadoDTO(empleado.empleado.empleado.id, empleado.empleado.empleado.nombreEmpleado, empleado.empleado.empleado.apellidoEmpleado, empleado.empleado.empleado.telefonoEmpleado,
                empleado.empleado.empleado.dniEmpleado, empleado.empleado.empleado.fechaRegistro, empleado.usuario, empleado.empleado.cargo)
            val comandaDTO = ComandaDTO(comandaId.toLong(), comandaRecibida.cantidadAsientos, subTOTAL, comandaRecibida.fechaRegistro,
            comandabean.comanda.comanda.mesa, comandabean.estadoComanda, empleadoDTO)
            comandaDTO.listaDetalleComanda = detalleComandaDTOS
            //FIREBASE COMANDA
            val empleadoNoSql : EmpleadoNoSql = EmpleadoNoSql(empleado.empleado.empleado.nombreEmpleado,empleado.empleado.empleado.apellidoEmpleado,
                empleado.empleado.empleado.telefonoEmpleado, empleado.empleado.empleado.dniEmpleado, empleado.empleado.empleado.fechaRegistro,
                UsuarioNoSql(empleado.usuario.correo), CargoNoSql(empleado.empleado.cargo.cargo)
            )
            val comandaNoSql : ComandaNoSql = ComandaNoSql(comandaRecibida.cantidadAsientos, comandaRecibida.precioTotal, fechaFormateada,
                MesaNoSql(comandabean.comanda.comanda.mesa.cantidadAsientos, comandabean.comanda.comanda.mesa.estado), EstadoComandaNoSql("Pagada"), empleadoNoSql)
            comandaNoSql.listaDetalleComanda = detalleComandaNoSql
            bdFirebase.child("comanda").child(comandaRecibida.id.toString()).setValue(comandaNoSql)
            //GENERAR CDP MYSQL
            val comprobanteMySql = ComprobanteDTO(fechaEmision = fechaFormateada, precioTotalPedido = totalPagar, igv = igv, subTotal = subTOTAL,
            descuento = descuento!!, nombreCliente = clienteNombre, comanda = comandaDTO, empleado = empleadoDTO,
            caja = caja, metodopago = metodoPago, tipoComprobante = tipoComprobante)
            Log.d("COMPROBANTE", "" + comprobanteMySql)
            grabarComprobanteMySQL(comprobanteMySql)
            //GENERAR CDP FIREBASE
            val empleadoSESION = EmpleadoGlobal.empleado.empleado
            val empleadoNoSqlFactura : EmpleadoNoSql = EmpleadoNoSql(empleadoSESION.nombreEmpleado,empleadoSESION.apellidoEmpleado, empleadoSESION.telefonoEmpleado, empleadoSESION.dniEmpleado, empleadoSESION.fechaRegistro,
                UsuarioNoSql(EmpleadoGlobal.usuario.correo), CargoNoSql(EmpleadoGlobal.empleado.cargo.cargo)
            )

            val establecimientoNoSql = caja.establecimiento?.let {
                EstablecimientoNoSql(
                    it.nomEstablecimiento,
                    it.telefonoestablecimiento,
                    it.direccionestablecimiento,
                    it.rucestablecimiento
                )
            }
            val comprobanteNoSql = ComprobanteNoSql(fechaEmision = fechaFormateada, precioTotalPedido = totalPagar, igv = igv, subTotal = subTOTAL,
                descuento = descuento!!, nombreCliente = clienteNombre, comanda = comandaNoSql, empleado = empleadoNoSqlFactura,
                caja = CajaNoSql(establecimientoNoSql!!), metodoPago = MetodoPagoNoSql(metodoPago.nombreMetodoPago), tipoComprobante = TipoComprobanteNoSql(tipoComprobante.tipo))
            bdFirebase.child("comprobante").child(idCDP.toString()).setValue(comprobanteNoSql)

            mostrarToast("Comprobante generado")
            cajaIntent()

        }
    }
    fun grabarComprobanteMySQL(bean: ComprobanteDTO){
        apiComprobante.fetchGuardarComprobante(bean).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }
    fun actualizarMesaMysql(bean: Mesa){
        apiMesa.fetchActualizarMesa(bean).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }

    fun cajaIntent(){
        if(VariablesGlobales.empleado?.empleado?.cargo?.id?.toInt() == 3){
            val intent = Intent(this, DatosComprobantes::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, ComandasVista::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun volver() {
        val intent = Intent(this, EditarComanda::class.java)
        intent.putExtra("Comanda", comandabean)
        startActivity(intent)
        finish()
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    fun conectar(){
        //Iniciar firebase en la clase actual
        FirebaseApp.initializeApp(this)
        bdFirebase = FirebaseDatabase.getInstance().reference
    }

}