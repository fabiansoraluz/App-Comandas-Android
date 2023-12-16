package com.example.project_kotlin.vistas.comandas

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.comandas.DetalleComandaAdapter
import com.example.project_kotlin.dao.*
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.*
import com.example.project_kotlin.entidades.dto.*
import com.example.project_kotlin.entidades.firebase.*
import com.example.project_kotlin.service.ApiServiceComanda
import com.example.project_kotlin.utils.ApiUtils
import com.example.project_kotlin.utils.VariablesGlobales
import com.example.project_kotlin.utils.appConfig
import com.google.errorprone.annotations.Var
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class RegistrarComanda : AppCompatActivity(), DetalleComandaAdapter.OnItemClickLister {
    private lateinit var  btnAgregarPDetalle : Button
    private lateinit var spnMesas : Spinner
    private lateinit var spnCategoriaPlatoC : Spinner
    private lateinit var spnPlatoC : Spinner
    private lateinit var edPlatoModificar : TextView
    private lateinit var edtComensalR : EditText
    private lateinit var btnGenerarComanda : Button
    private lateinit var edtEstadoComandaR : EditText
    private lateinit var edtPrecioTotalR : EditText
    private lateinit var edtEmpleadoR : EditText
    private lateinit var rvDetalleComandaR : RecyclerView
    private lateinit var btnAniadirPlato : Button
    private lateinit var btnRegresarCR : Button
    private lateinit var bdFirebase : DatabaseReference
    private lateinit var apiComanda : ApiServiceComanda

    //BASE DE DATOS
    private lateinit var mesaDao : MesaDao
    private lateinit var comandaDao : ComandaDao
    private lateinit var categoriaDao : CategoriaPlatoDao
    private lateinit var platoDao : PlatoDao
    private lateinit var detalleDao : DetalleComandaDao
    //Entidades globales para guardar
    private lateinit var EmpleadoGlobal : EmpleadoUsuarioYCargo
    private  var detalleComandaGlobal : MutableList<DetalleComandaConPlato> = mutableListOf()
    //ADAPTADOR
    private lateinit var adaptadorDetalle : DetalleComandaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar_comanda_form)

        //validaciones de roles
        btnGenerarComanda = findViewById(R.id.btnGenerarComandaR)
        btnRegresarCR = findViewById(R.id.btnRegresarCR)
        btnAniadirPlato = findViewById(R.id.btnAniadirPlatoR)
        spnMesas = findViewById(R.id.spnMesasRComanda)
        edtComensalR = findViewById(R.id.edtComensalR)
        edtEstadoComandaR = findViewById(R.id.edtEstadoComandaR)
        edtPrecioTotalR = findViewById(R.id.edtPrecioTotalR)
        edtEmpleadoR = findViewById(R.id.edtEmpleadoR)
        mesaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).mesaDao()
        categoriaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        platoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).platoDao()
        comandaDao =  ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comandaDao()
        detalleDao =  ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).detalleComandaDao()
        rvDetalleComandaR = findViewById(R.id.rvDetalleComanda)
        btnAniadirPlato.setOnClickListener{
            val message : String? ="Agregar plato"
            dialogAgregarPlato(message)
        }
        btnRegresarCR.setOnClickListener({volver()})
        btnGenerarComanda.setOnClickListener({generarComanda()})
        if(VariablesGlobales.empleado != null){
            EmpleadoGlobal = VariablesGlobales.empleado!!
        }
        apiComanda = ApiUtils.getApiServiceComanda()
        edtEmpleadoR.setText(EmpleadoGlobal.empleado.empleado.nombreEmpleado + " " + EmpleadoGlobal.empleado.empleado.apellidoEmpleado)
        cargarMesasLibres()
        conectar()
    }
    fun generarComanda(){
        val numMesa = spnMesas.selectedItem.toString()

        val cantCli = edtComensalR.text.toString().toIntOrNull()
        val sumaPrecio = detalleComandaGlobal.sumOf { it.detalle.precioUnitario }
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val fechaActual = Date()
        val fechaFormateada = dateFormat.format(fechaActual)


        if(cantCli == null ||  cantCli !in 1..15){
            mostrarToast("Debes ingresar la cantidad de clientes y debe ser menor a 15")
            return
        }
        if(detalleComandaGlobal.size == 0){
            mostrarToast("No se puede generar una comanda sin platos")
            return
        }
        lifecycleScope.launch(Dispatchers.IO){
            val mesa = mesaDao.obtenerPorId(numMesa.toLong())

            val comandaAgregar = Comanda(cantidadAsientos = cantCli, precioTotal = sumaPrecio, mesaId = numMesa.toInt(),
                estadoComandaId = 1, fechaRegistro = fechaFormateada, empleadoId = VariablesGlobales.empleado?.empleado?.empleado?.id?.toInt()!!)
            val idComanda = comandaDao.guardar(comandaAgregar)
            //MYSQL - QUE TERRIBLE CÓDIGO D':
            //ACTUALIZAR MESA
            mesa.mesa.estado = "Ocupado"
            mesaDao.actualizar(mesa.mesa)
            val mesaDTO = Mesa(mesa.mesa.id, mesa.mesa.cantidadAsientos, mesa.mesa.estado)
            val empleado = EmpleadoGlobal.empleado.empleado
            val empleadoDTO = EmpleadoDTO(empleado.id, empleado.nombreEmpleado, empleado.apellidoEmpleado, empleado.telefonoEmpleado,
            empleado.dniEmpleado, empleado.fechaRegistro, EmpleadoGlobal.usuario, EmpleadoGlobal.empleado.cargo)
            val detalleComandaNoSql : MutableList<DetalleComandaNoSql> =  mutableListOf()
            val detalleComandaDTOS : MutableList<DetalleComandaDTO> =  mutableListOf()
            val comandaDTO = ComandaDTO(id = 0, cantidadAsientos = cantCli, precioTotal = sumaPrecio, mesa = mesaDTO,
            estadoComanda = EstadoComanda(1, "Libre"), fechaEmision = fechaFormateada, empleado = empleadoDTO)
            detalleComandaGlobal.forEach{detalleComanda ->
                val categoriaPlato = categoriaDao.obtenerPorId(detalleComanda.plato.catplato_id)
                //GUARDAR EN ROOM
                detalleDao.guardar(DetalleComanda(comandaId = idComanda.toInt(), idPlato = detalleComanda.plato.id,
                    cantidadPedido = detalleComanda.detalle.cantidadPedido, precioUnitario = detalleComanda.detalle.precioUnitario,
                    observacion = detalleComanda.detalle.observacion))
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
            comandaDTO.listaDetalleComanda = detalleComandaDTOS
            grabarComandaMySql(comandaDTO)
            //FIREBASE
            val empleadoNoSql : EmpleadoNoSql = EmpleadoNoSql(empleado.nombreEmpleado,empleado.apellidoEmpleado, empleado.telefonoEmpleado, empleado.dniEmpleado, empleado.fechaRegistro,
            UsuarioNoSql(EmpleadoGlobal.usuario.correo), CargoNoSql(EmpleadoGlobal.empleado.cargo.cargo)
            )
            val comandaNoSql : ComandaNoSql = ComandaNoSql(comandaAgregar.cantidadAsientos, comandaAgregar.precioTotal, fechaFormateada,
            MesaNoSql(mesa.mesa.cantidadAsientos, mesa.mesa.estado), EstadoComandaNoSql("Generada"), empleadoNoSql)
            comandaNoSql.listaDetalleComanda = detalleComandaNoSql
            bdFirebase.child("comanda").child(idComanda.toString()).setValue(comandaNoSql)
            mostrarToast("Comanda agregada correctamente")
            volver()
        }

    }
    fun grabarComandaMySql(bean:ComandaDTO){
        apiComanda.fetchGuardarComanda(bean).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }
    fun volver(){
        var intent = Intent(this, ComandasVista::class.java)
        startActivity(intent)
    }

    fun conectar(){
        //Iniciar firebase en la clase actual
        FirebaseApp.initializeApp(this)
        bdFirebase = FirebaseDatabase.getInstance().reference
    }

    //dialog Aniadir
    private fun dialogAgregarPlato(message: String?){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_agregarplato_c)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMensaje : TextView =  dialog.findViewById(R.id.tvMensajeDialog)
        spnCategoriaPlatoC  = dialog.findViewById(R.id.spnCategoriaPlatoC)
        spnPlatoC = dialog.findViewById(R.id.spnPlatoC)
        //Agregar categorías
        cargarCategoriasPlatos()
        spnCategoriaPlatoC.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cargarPlatosPorCategoria()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                btnAgregarPDetalle.isEnabled = false
            }
        })
        val edtCantidadPlatoC : EditText = dialog.findViewById(R.id.edtCantidadPlatoC)
        val edtObservacionPlatoD : EditText = dialog.findViewById(R.id.edtObservacionPlatoD)
        btnAgregarPDetalle  = dialog.findViewById(R.id.btnRegistrarPlatoD)
        val btnCancelacionPDetalle : Button = dialog.findViewById(R.id.btnCancelarPlatoD)

        tvMensaje.text = message

        btnAgregarPDetalle.setOnClickListener{
            lifecycleScope.launch(Dispatchers.IO){
                //Obtener el plato
                val cantidadInicialPlatos = detalleComandaGlobal.size

                val nombre = spnPlatoC.selectedItem.toString()
                Log.d("Plato", "" + nombre)
                val platoDato = platoDao.obtenerPorNombre(nombre)

                val cantidadValidar = edtCantidadPlatoC.text.toString().toIntOrNull()
                if(cantidadValidar == null || cantidadValidar !in 1..50) {
                    mostrarToast("Ingrese una cantidad valida entre 1 a 50")
                    return@launch
                }
                //Obtener valores de los inputs
                val cantidadPedido = edtCantidadPlatoC.text.toString().toInt()
                val precioUnitario = platoDato.precioPlato * cantidadPedido
                var observacion = edtObservacionPlatoD.text.toString()
                val detalleExistente = detalleComandaGlobal.find{ it.plato.id == platoDato.id}
                if(detalleExistente != null){
                    val sumamatorio = detalleComandaGlobal.sumOf { it.detalle.precioUnitario }
                    detalleExistente.detalle.precioUnitario += sumamatorio
                    detalleExistente.detalle.observacion += observacion
                    detalleExistente.detalle.cantidadPedido+= cantidadPedido
                }else {
                    val detalle = DetalleComanda(comandaId = 0, cantidadPedido = cantidadPedido, precioUnitario = precioUnitario, idPlato = platoDato.id, observacion = observacion)
                    val detalleAgregar = DetalleComandaConPlato(detalle, platoDato)
                    detalleComandaGlobal.add(detalleAgregar)
                }

                withContext(Dispatchers.Main) {
                    if (cantidadInicialPlatos == 0){
                        adaptadorDetalle = DetalleComandaAdapter(detalleComandaGlobal, this@RegistrarComanda)
                        rvDetalleComandaR.layoutManager = LinearLayoutManager(appConfig.CONTEXT)
                        rvDetalleComandaR.adapter = adaptadorDetalle
                    }else{
                        adaptadorDetalle.actualizarDetalleComanda(detalleComandaGlobal)
                    }
                    val sumaPrecio = detalleComandaGlobal.sumOf { it.detalle.precioUnitario }
                    edtPrecioTotalR.setText(sumaPrecio.toString())
                    dialog.dismiss()

                }
            }
        }
        btnCancelacionPDetalle.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
    fun cargarCategoriasPlatos(){
        lifecycleScope.launch(Dispatchers.IO){
            var categorias = categoriaDao.obtenerTodo()
            if(categorias.size > 0){
                val categorias = categorias.map { it.categoria }
                val adapter = ArrayAdapter(this@RegistrarComanda, android.R.layout.simple_spinner_item, categorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnCategoriaPlatoC.adapter = adapter
                cargarPlatosPorCategoria()

            } else {
                val opcion = "No hay categorias"
                val adapter = ArrayAdapter(this@RegistrarComanda, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                spnCategoriaPlatoC.adapter = adapter
                runOnUiThread {
                    btnAgregarPDetalle.isEnabled = false
                }

            }

        }
    }
    private fun cargarPlatosPorCategoria(){
        lifecycleScope.launch(Dispatchers.IO){

            var numero = (spnCategoriaPlatoC.selectedItemPosition+1)
            var idCatPlato = "C-${String.format("%03d", numero)}"
            var platos = platoDao.obtenerPlatosPorCategoria(idCatPlato)

            if(platos.size > 0){
                val platos = platos.map { it.plato.nombrePlato }
                val adapter = ArrayAdapter(this@RegistrarComanda, android.R.layout.simple_spinner_item, platos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                runOnUiThread {
                btnAgregarPDetalle.isEnabled = true
                spnPlatoC.adapter = adapter
                }

            } else {
                val opcion = "No hay platos"
                val adapter = ArrayAdapter(this@RegistrarComanda, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                runOnUiThread {
                    spnPlatoC.adapter = adapter
                    btnAgregarPDetalle.isEnabled = false
                }

            }

        }

    }
    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    fun cargarMesasLibres(){
        lifecycleScope.launch(Dispatchers.IO){
            var mesas = mesaDao.obtenerMesasLibres()
            if(mesas.size > 0){
                val mesasID = mesas.map { it.id }
                val adapter = ArrayAdapter(this@RegistrarComanda, android.R.layout.simple_spinner_item, mesasID)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnMesas.adapter = adapter

            }else {
                val opcion = "No hay mesas libres"
                val adapter = ArrayAdapter(this@RegistrarComanda, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                spnMesas.adapter = adapter

                mostrarToast("No hay mesas libres")
                runOnUiThread {
                    btnGenerarComanda.isEnabled = false
                    btnAniadirPlato.isEnabled = false
                }

            }

        }
    }

    override fun onItemDeleteClick(detalle: DetalleComandaConPlato) {
        mostrarToast("HOLA")
        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("Eliminar detalle")
            .setPositiveButton("Eliminar") { _, _ ->
                detalleComandaGlobal.remove(detalle)
                adaptadorDetalle.actualizarDetalleComanda(detalleComandaGlobal)
                val sumaPrecio = detalleComandaGlobal.sumOf { it.detalle.precioUnitario }
                edtPrecioTotalR.setText(sumaPrecio.toString())
                edtPrecioTotalR.setText(sumaPrecio.toString())

            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    override fun onItemUpdateClick(detalle: DetalleComandaConPlato) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_agregarplato_c)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMensaje : TextView =  dialog.findViewById(R.id.tvMensajeDialog)
        spnCategoriaPlatoC  = dialog.findViewById(R.id.spnCategoriaPlatoC)
        spnPlatoC = dialog.findViewById(R.id.spnPlatoC)
        edPlatoModificar = dialog.findViewById(R.id.edtPlatoC)
        edPlatoModificar.visibility = View.VISIBLE
        spnPlatoC.visibility = View.GONE
        spnCategoriaPlatoC.visibility = View.GONE
        val edtCantidadPlatoC : EditText = dialog.findViewById(R.id.edtCantidadPlatoC)

        val edtObservacionPlatoD : EditText = dialog.findViewById(R.id.edtObservacionPlatoD)
        btnAgregarPDetalle  = dialog.findViewById(R.id.btnRegistrarPlatoD)
        btnAgregarPDetalle.setText("Modificar plato")
        val btnCancelacionPDetalle : Button = dialog.findViewById(R.id.btnCancelarPlatoD)
        tvMensaje.text = "Modificar plato"
        //AGREGAR DATOS
        edPlatoModificar.setText(detalle.plato.nombrePlato)
        edtCantidadPlatoC.setText(detalle.detalle.cantidadPedido.toString())
        edtObservacionPlatoD.setText(detalle.detalle.observacion)

        btnAgregarPDetalle.setOnClickListener{
            val cantidad = edtCantidadPlatoC.text.toString().toIntOrNull()
            val observacion = edtObservacionPlatoD.text.toString()
            if(cantidad == null || cantidad !in 1..50) {
                mostrarToast("Ingrese una cantidad valida entre 1 a 50")
                return@setOnClickListener
            }
            val detalleExistente = detalleComandaGlobal.find{ it.plato.id == detalle.plato.id}
            if(detalleExistente!= null){
                detalleExistente.detalle.cantidadPedido = cantidad
                detalleExistente.detalle.observacion = observacion
                detalleExistente.detalle.precioUnitario = cantidad * detalleExistente.plato.precioPlato
                adaptadorDetalle.actualizarDetalleComanda(detalleComandaGlobal)
                val sumaPrecio = detalleComandaGlobal.sumOf { it.detalle.precioUnitario }
                edtPrecioTotalR.setText(sumaPrecio.toString())
                dialog.dismiss()
            }
        }


        btnCancelacionPDetalle.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }


}