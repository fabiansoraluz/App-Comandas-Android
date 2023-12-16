package com.example.project_kotlin.vistas.comandas

import android.annotation.SuppressLint
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
import com.example.project_kotlin.entidades.dto.ComandaDTO
import com.example.project_kotlin.entidades.dto.DetalleComandaDTO
import com.example.project_kotlin.entidades.dto.EmpleadoDTO
import com.example.project_kotlin.entidades.dto.PlatoDTO
import com.example.project_kotlin.entidades.firebase.*
import com.example.project_kotlin.service.ApiServiceComanda
import com.example.project_kotlin.service.ApiServiceMesa
import com.example.project_kotlin.utils.ApiUtils
import com.example.project_kotlin.utils.VariablesGlobales
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.facturar.FacturarActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditarComanda : AppCompatActivity(), DetalleComandaAdapter.OnItemClickLister {
    private lateinit var edtNumMesa: EditText
    private lateinit var edtComensal: EditText
    private lateinit var edtEstadoComanda: EditText
    private lateinit var edtPrecioTotal: EditText
    private lateinit var edtEmpleado: EditText
    private lateinit var rvDetalleComandaR : RecyclerView
    private lateinit var btnAniadirPlato: Button
    private lateinit var btnRegresarC: Button
    private lateinit var rvDetalleComanda: RecyclerView
    private lateinit var bdFirebase: DatabaseReference
    private lateinit var spnCategoriaPlatoC:Spinner
    private lateinit var spnPlatoC:Spinner
    private lateinit var  btnAgregarPDetalle : Button
    private lateinit var btnFacturar:Button
    private lateinit var edPlatoModificar : TextView
    private lateinit var btnActualizarComanda:Button
    private  lateinit var btnEliminar:Button


    private lateinit var apiComanda: ApiServiceComanda
    private lateinit var apiMesa:ApiServiceMesa

    //Llamando al objeto comandas con mesa, empleados y estadocomanda
    private lateinit var comandabean: ComandaMesaYEmpleadoYEstadoComanda

    //BASE DE DATOS
    private lateinit var mesaDao: MesaDao
    private lateinit var comandaDao: ComandaDao
    private lateinit var categoriaDao: CategoriaPlatoDao
    private lateinit var platoDao: PlatoDao
    private lateinit var detalleDao: DetalleComandaDao
    private lateinit var empleadoDao:EmpleadoDao

    //Entidades globales para guardar

    private lateinit var EmpleadoGlobal: EmpleadoUsuarioYCargo
    private var detalleComandaGlobal: MutableList<DetalleComandaConPlato> = mutableListOf()
    private var detalleComandaActual: MutableList<DetalleComandaConPlato> = mutableListOf()


    //ADAPTADOR
    private lateinit var adaptadorDetalle: DetalleComandaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actualizar_comanda_form)
        conectar()
        edtNumMesa = findViewById(R.id.edtNumeroMesa)
        edtComensal = findViewById(R.id.edtComensal)
        edtEstadoComanda = findViewById(R.id.edtEstadoComanda)
        edtPrecioTotal = findViewById(R.id.edtPrecioTotal)
        edtEmpleado = findViewById(R.id.edtEmpleado)
        btnRegresarC = findViewById(R.id.btnRegresarC)
        btnAniadirPlato=findViewById(R.id.btnAniadirPlato)
        btnActualizarComanda=findViewById(R.id.btnActualizarComanda)
        btnFacturar=findViewById(R.id.btnFacturar)
        rvDetalleComanda = findViewById(R.id.rvDetalleComanda)
        rvDetalleComandaR = findViewById(R.id.rvDetalleComanda)
        btnEliminar=findViewById(R.id.btnEliminarComanda)


        apiComanda=ApiUtils.getApiServiceComanda()
        apiMesa=ApiUtils.getAPIServiceMesa()

        btnRegresarC.setOnClickListener({ volver() })

        //Cargar comanda con datos
        empleadoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).empleadoDao()
        categoriaDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        comandaDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comandaDao()
        detalleDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).detalleComandaDao()
        platoDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).platoDao()
        comandabean = intent.getSerializableExtra("Comanda") as ComandaMesaYEmpleadoYEstadoComanda
        mesaDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).mesaDao()
        cargarDatos()
        cargarDetalleComandas()
        btnAniadirPlato.setOnClickListener {
            val message: String? = "Agregar plato"
            dialogAgregarPlato(message)
        }
        btnFacturar.setOnClickListener({factuarIndex()})
        btnRegresarC.setOnClickListener({ volver() })
        btnActualizarComanda.setOnClickListener({actualizarComanda()})
        btnEliminar.setOnClickListener({Eliminar()})

    }

    @SuppressLint("SuspiciousIndentation")
    fun actualizarComanda(){

        val numeroMesa=edtNumMesa.text.toString()
        lifecycleScope.launch(Dispatchers.IO) {
            val cantidadDePersonas = edtComensal.text.toString().toInt()
            val cantidadAsientos = comandabean.comanda.comanda.mesa.cantidadAsientos

            val sumaPrecio = detalleComandaActual.sumOf { it.detalle.precioUnitario }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val fechaActual = Date()
            val fechaFormateada = dateFormat.format(fechaActual)


            if(cantidadDePersonas==null){
                mostrarToast(" Error: la cantidad de personas no es un número válido")
                return@launch
            }
            if (cantidadDePersonas>cantidadAsientos){
                mostrarToast(" Error: la cantidad de personas no debe ser mayor al numero de la cantidad de asiento")
                return@launch
            }

            if(detalleComandaGlobal.size == 0){
                mostrarToast("No se puede generar una comanda sin platos")
                return@launch
            }

            lifecycleScope.launch(Dispatchers.IO){
                val mesa = mesaDao.obtenerPorId(numeroMesa.toLong())
                val empleado = EmpleadoGlobal.empleado.empleado
                val mesaDTO = Mesa(mesa.mesa.id, mesa.mesa.cantidadAsientos, mesa.mesa.estado)


                comandabean.comanda.comanda.comanda.cantidadAsientos=cantidadDePersonas
                comandabean.comanda.comanda.comanda.precioTotal=sumaPrecio
                comandabean.comanda.comanda.comanda.empleadoId=empleado.id.toInt()
                comandabean.comanda.comanda.comanda.fechaRegistro=fechaFormateada
                comandaDao.actualizar(comandabean.comanda.comanda.comanda)

                val empleadoDTO = EmpleadoDTO(empleado.id, empleado.nombreEmpleado, empleado.apellidoEmpleado, empleado.telefonoEmpleado,
                    empleado.dniEmpleado, empleado.fechaRegistro, EmpleadoGlobal.usuario, EmpleadoGlobal.empleado.cargo)
                val detalleComandaDTOS : MutableList<DetalleComandaDTO> =  mutableListOf()
                val detalleComandaNoSql : MutableList<DetalleComandaNoSql> =  mutableListOf()
                val comandaDTO = ComandaDTO(comandabean.comanda.comanda.comanda.id, cantidadAsientos = cantidadDePersonas, precioTotal = sumaPrecio, mesa = mesaDTO,
                    estadoComanda = EstadoComanda(1, "Generado"), fechaEmision = fechaFormateada, empleado = empleadoDTO)
                detalleComandaGlobal.forEach { detalleComanda ->
                    val categoriaPlato = categoriaDao.obtenerPorId(detalleComanda.plato.catplato_id)
                    val detalleparaCambiar=detalleComandaActual.find{ it.plato.id==detalleComanda.plato.id}
                    Log.d("tamaño 1",""+detalleparaCambiar)
                    val platoDTO = PlatoDTO(detalleComanda.plato.id, detalleComanda.plato.nombrePlato, detalleComanda.plato.nombreImagen,
                        detalleComanda.plato.precioPlato, CategoriaPlato(id = detalleComanda.plato.catplato_id, categoria =categoriaPlato.categoria))
                    if(detalleparaCambiar==null){
                        mostrarToast("Entra aca y elimina el detalle")
                        detalleDao.eliminar(detalleComanda.detalle)
                    }else{
                        if (detalleparaCambiar.detalle.id.toInt()!=0){
                            detalleComandaDTOS.add(DetalleComandaDTO(id=detalleComanda.detalle.id.toInt(), cantidadPedido = detalleComanda.detalle.cantidadPedido,
                                precioUnitario = detalleComanda.detalle.precioUnitario, observacion = detalleComanda.detalle.observacion, plato = platoDTO))
                            //GUARDAR PARA FIREBASE
                            detalleComandaNoSql.add(DetalleComandaNoSql(cantidadPedido = detalleComanda.detalle.cantidadPedido, precioUnitario = detalleComanda.detalle.precioUnitario,
                                observacion = detalleComanda.detalle.observacion, PlatoNoSql(platoDTO.nombre,platoDTO.imagen, platoDTO.precioPlato, CategoriaPlatoNoSql(categoria = categoriaPlato.categoria))
                            ))
                            //bdFirebase.child("comanda").child(detalleComanda.detalle.id.toString()).setValue(detalleComandaNoSql)
                            detalleDao.actualizar(detalleComanda.detalle)
                        }
                    }
                }
                detalleComandaActual.forEach{nuevoDetalleComanda->
                    val existeComanda=detalleComandaGlobal.find { it.detalle.idPlato==nuevoDetalleComanda.plato.id }
                    val categoriaPlato = categoriaDao.obtenerPorId(nuevoDetalleComanda.plato.catplato_id)
                    if(existeComanda==null){
                        Log.d("tamaño 2",""+existeComanda)
                        val platoDTO = PlatoDTO(nuevoDetalleComanda.plato.id, nuevoDetalleComanda.plato.nombrePlato, nuevoDetalleComanda.plato.nombreImagen,
                            nuevoDetalleComanda.plato.precioPlato, CategoriaPlato(id = nuevoDetalleComanda.plato.catplato_id, categoria =categoriaPlato.categoria))
                        detalleComandaDTOS.add(
                            DetalleComandaDTO(0, cantidadPedido = nuevoDetalleComanda.detalle.cantidadPedido,
                                precioUnitario = nuevoDetalleComanda.detalle.precioUnitario, observacion = nuevoDetalleComanda.detalle.observacion,
                                plato = platoDTO)
                        )
                        detalleComandaNoSql.add(
                            DetalleComandaNoSql(cantidadPedido = nuevoDetalleComanda.detalle.cantidadPedido, precioUnitario = nuevoDetalleComanda.detalle.precioUnitario,
                                observacion = nuevoDetalleComanda.detalle.observacion, PlatoNoSql(platoDTO.nombre,platoDTO.imagen, platoDTO.precioPlato, CategoriaPlatoNoSql(categoria = categoriaPlato.categoria))
                            ))
                        // bdFirebase.child("comanda").child(nuevoDetalleComanda.detalle.id.toString()).setValue(detalleComandaNoSql)
                        detalleDao.guardar(nuevoDetalleComanda.detalle)

                    }
                }
                comandaDTO.listaDetalleComanda=detalleComandaDTOS
                actualizarMysql(comandaDTO,comandaDTO.id.toInt())
                mostrarToast("Comanda modificado correctamente")

            }
        }

    }

    fun Eliminar(){

        val mensaje: AlertDialog.Builder = AlertDialog.Builder(this)
        mensaje.setTitle("Sistema comandas")
        mensaje.setMessage("¿Seguro de eliminar?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                comandaDao.eliminar(comandabean.comanda.comanda.comanda)
                EliminarMysql(comandabean.comanda.comanda.comanda.id)
                bdFirebase.child("comanda").child(comandabean.comanda.comanda.comanda.id.toString()).removeValue()
                comandabean.comanda.comanda.mesa.estado = "Libre"
                mesaDao.actualizar(comandabean.comanda.comanda.mesa)
                actualizarMesaMysql(comandabean.comanda.comanda.mesa)
                bdFirebase.child("Mesa").child(comandabean.comanda.comanda.mesa.id.toString()).setValue(MesaNoSql(comandabean.comanda.comanda.mesa.cantidadAsientos, comandabean.comanda.comanda.mesa.estado))
                mostrarToast("Comanda elimina correctamente")
                volver()
            }
        }
        mensaje.setNegativeButton("Cancelar") { _, _ -> }
        mensaje.setIcon(android.R.drawable.ic_delete)
        mensaje.show()
    }

    fun actualizarMysql(bean:ComandaDTO,codigo: Int){

        apiComanda.fetchActualizarComandayDetalleComanda(codigo,bean).enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Comanda actualizada correctamente")
                volver()
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


    fun EliminarMysql(codigo: Long){
        apiComanda.fectDeleteComandayDetalleComanda(codigo.toInt()).enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Comandaeliminada")
                volver()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })

    }

    fun cargarDetalleComandas(){
        lifecycleScope.launch(Dispatchers.IO) {
            // Obtener los detalles de la comanda específica
            var detallesComanda = detalleDao.buscarDetallesPorComanda(comandabean.comanda.comanda.comanda.id)
            withContext(Dispatchers.Main) {
                if (detallesComanda != null) {
                    detalleComandaGlobal.addAll(detallesComanda)
                    detalleComandaActual.addAll(detalleComandaGlobal)
                    adaptadorDetalle = DetalleComandaAdapter(detallesComanda, this@EditarComanda)
                    rvDetalleComandaR.layoutManager = LinearLayoutManager(appConfig.CONTEXT)
                    rvDetalleComanda.adapter = adaptadorDetalle
                }
            }
        }
    }
    fun cargarDatos(){
        if (comandabean != null) {
            print("Entro aquí")
            if(VariablesGlobales.empleado != null){
                EmpleadoGlobal = VariablesGlobales.empleado!!
            }
            // Accede a los campos del objeto ComandaMesaYEmpleadoYEstadoComanda
            edtNumMesa.setText(comandabean.comanda.comanda.comanda.mesaId.toString())
            edtComensal.setText(comandabean.comanda.comanda.comanda.cantidadAsientos.toString())
            edtPrecioTotal.setText(comandabean.comanda.comanda.comanda.precioTotal.toString())
            edtEstadoComanda.setText(comandabean.estadoComanda.estadoComanda)
            edtEmpleado.setText(EmpleadoGlobal.empleado.empleado.nombreEmpleado + " " + EmpleadoGlobal.empleado.empleado.apellidoEmpleado)

            // Otros campos
        } else {
            print("No hay datos en comandas")
            // El objeto ComandaMesaYEmpleadoYEstadoComanda es nulo
        }
    }

    fun cargarCategorias(){
        lifecycleScope.launch(Dispatchers.IO){
            var categorias = categoriaDao.obtenerTodo()
            if(categorias.size > 0){
                val categorias = categorias.map { it.categoria }
                val adapter = ArrayAdapter(this@EditarComanda, android.R.layout.simple_spinner_item, categorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnCategoriaPlatoC.adapter = adapter
                cargarPlatosPorCategoria()

            } else {
                val opcion = "No hay categorias"
                val adapter = ArrayAdapter(this@EditarComanda, android.R.layout.simple_spinner_item, arrayOf(opcion)) // Crea un adaptador con la opción única
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Establece el layout para las opciones desplegables
                spnCategoriaPlatoC.adapter = adapter
                runOnUiThread {
                    btnAgregarPDetalle.isEnabled = false
                }

            }

        }


    }

    private fun cargarPlatosPorCategoria() {
        lifecycleScope.launch(Dispatchers.IO) {
            var numero = (spnCategoriaPlatoC.selectedItemPosition + 1)
            var idCatPlato = "C-${String.format("%03d", numero)}"
            var platos = platoDao.obtenerPlatosPorCategoria(idCatPlato)

            if (platos.size > 0) {
                val platosNombres = platos.map { it.plato.nombrePlato }
                val adapter = ArrayAdapter(this@EditarComanda, android.R.layout.simple_spinner_item, platosNombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                runOnUiThread {
                    spnPlatoC.adapter = adapter
                    btnAgregarPDetalle.isEnabled = true
                }
            } else {
                val opcion = "No hay platos"
                val adapter = ArrayAdapter(this@EditarComanda, android.R.layout.simple_spinner_item, arrayOf(opcion))
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                runOnUiThread {
                    spnPlatoC.adapter = adapter
                    btnAgregarPDetalle.isEnabled = false
                }
            }
        }
    }


    //dialog Aniadir
    private fun dialogAgregarPlato(message: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_agregarplato_c)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnAgregarPDetalle  = dialog.findViewById(R.id.btnRegistrarPlatoD)
        val tvMensaje: TextView = dialog.findViewById(R.id.tvMensajeDialog)
        spnCategoriaPlatoC=dialog.findViewById(R.id.spnCategoriaPlatoC)
        spnPlatoC = dialog.findViewById(R.id.spnPlatoC)
        cargarCategorias()
        spnCategoriaPlatoC.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cargarPlatosPorCategoria()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                btnAgregarPDetalle.isEnabled = false
            }
        })

        val edtCantidadPlatoC: EditText = dialog.findViewById(R.id.edtCantidadPlatoC)
        val edtObservacionPlatoD: EditText = dialog.findViewById(R.id.edtObservacionPlatoD)
        val btnAgregarPDetalle: Button = dialog.findViewById(R.id.btnRegistrarPlatoD)
        val btnCancelacionPDetalle: Button = dialog.findViewById(R.id.btnCancelarPlatoD)
        tvMensaje.text = message

        btnAgregarPDetalle.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                //Obtener el plato
                val cantidadInicialPlatos = detalleComandaActual.size
                val nombre = spnPlatoC.selectedItem.toString()
                val platoDato = platoDao.obtenerPorNombre(nombre)
                val cantidadValidar = edtCantidadPlatoC.text.toString().toIntOrNull()
                if (cantidadValidar == null) {
                    mostrarToast("Ingrese una cantidad")
                    return@launch
                }
                //Obtener valores de los inputs
                val cantidadPedido = edtCantidadPlatoC.text.toString().toInt()
                val precioUnitario = platoDato.precioPlato * cantidadPedido
                var observacion = edtObservacionPlatoD.text.toString()

                val detalleExistente = detalleComandaActual.find { it.plato.id == platoDato.id }
                if (detalleExistente != null) {
                    val sumamatorio = detalleComandaActual.sumOf { it.detalle.precioUnitario }
                    detalleExistente.detalle.precioUnitario += sumamatorio
                    detalleExistente.detalle.observacion += observacion
                    detalleExistente.detalle.cantidadPedido += cantidadPedido

                } else {
                    val detalle = DetalleComanda(
                        comandaId = comandabean.comanda.comanda.comanda.id.toInt(),
                        cantidadPedido = cantidadPedido,
                        precioUnitario = precioUnitario,
                        idPlato = platoDato.id,
                        observacion = observacion
                    )
                    val detalleAgregar = DetalleComandaConPlato(detalle, platoDato)
                    detalleComandaActual.add(detalleAgregar)
                }
                withContext(Dispatchers.Main) {
                    if (cantidadInicialPlatos == 0) {
                        adaptadorDetalle = DetalleComandaAdapter(detalleComandaActual, this@EditarComanda)
                        rvDetalleComandaR.layoutManager = LinearLayoutManager(appConfig.CONTEXT)
                        rvDetalleComandaR.adapter = adaptadorDetalle
                    } else {
                        adaptadorDetalle.actualizarDetalleComanda(detalleComandaActual)
                    }
                    val sumaPrecio = detalleComandaActual.sumOf { it.detalle.precioUnitario }
                    edtPrecioTotal.setText(sumaPrecio.toString())
                    dialog.dismiss()

                }
            }

        }
        btnCancelacionPDetalle.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun volver(){
        var intent = Intent(this, ComandasVista::class.java)
        startActivity(intent)
    }

    fun factuarIndex(){
        var intentFacturar=Intent(this,FacturarActivity::class.java)
        intentFacturar.putExtra("comandaFacturar", comandabean)
        startActivity(intentFacturar)
    }
    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemDeleteClick(detalle: DetalleComandaConPlato) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("Eliminar detalle")
            .setPositiveButton("Eliminar") { _, _ ->
                detalleComandaActual.remove(detalle)
                adaptadorDetalle.actualizarDetalleComanda(detalleComandaActual)
                val sumaPrecio = detalleComandaActual.sumOf { it.detalle.precioUnitario }
                edtPrecioTotal.setText(sumaPrecio.toString())
                edtPrecioTotal.setText(sumaPrecio.toString())

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
            if(cantidad == null){
                mostrarToast("Debes ingresar una cantidad")
                return@setOnClickListener
            }

            val detalleExistente = detalleComandaActual.find{ it.plato.id == detalle.plato.id}
            if(detalleExistente!= null){
                detalleExistente.detalle.cantidadPedido = cantidad
                detalleExistente.detalle.observacion = observacion
                detalleExistente.detalle.precioUnitario = cantidad * detalleExistente.plato.precioPlato
                adaptadorDetalle.actualizarDetalleComanda(detalleComandaActual)
                val sumaPrecio = detalleComandaActual.sumOf { it.detalle.precioUnitario }
                edtPrecioTotal.setText(sumaPrecio.toString())

                dialog.dismiss()
            }
        }

        btnCancelacionPDetalle.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }
    fun conectar(){
        //inicar mi firebase
        FirebaseApp.initializeApp(this)
        bdFirebase= FirebaseDatabase.getInstance().reference
    }


}