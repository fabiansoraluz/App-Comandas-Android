package com.example.project_kotlin.vistas.empleados

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.empleado.EmpleadoAdapter
import com.example.project_kotlin.dao.CargoDao
import com.example.project_kotlin.dao.EmpleadoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.EmpleadoUsuarioYCargo
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.ConfiguracionVista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DatosEmpleados: AppCompatActivity() {
    private lateinit var btnVolver : Button
    private lateinit var  btnAgregar : Button
    private lateinit var btnFiltrar : Button
    private lateinit var empleadoDao : EmpleadoDao
    private lateinit var cargoDao : CargoDao
    private lateinit var adaptador : EmpleadoAdapter
    private lateinit var spCargos : Spinner
    private lateinit var rvEmpleados : RecyclerView
    private lateinit var edNombreFiltro : EditText
    private lateinit var edFechaFiltro : EditText
    private lateinit var imgBorrarFecha : ImageButton
    private lateinit var tvEtiqueta : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_usuarios)
        btnAgregar = findViewById(R.id.btnNuevoEmpleadoCon)
        imgBorrarFecha = findViewById(R.id.btnBorrarFecha)
        btnVolver = findViewById(R.id.btnCancelarCategoria)
        spCargos = findViewById(R.id.spnCargoEmpleadoE)
        btnFiltrar = findViewById(R.id.btnAplicarUsu)
        rvEmpleados = findViewById(R.id.rvPlatos)
        btnVolver.setOnClickListener({volver()})
        tvEtiqueta = findViewById(R.id.tvCategoriaSinDatos)
        btnAgregar.setOnClickListener({agregar()})
        empleadoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).empleadoDao()
        cargoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cargoDao()
        btnFiltrar.setOnClickListener({filtrar()})
        edFechaFiltro = findViewById(R.id.edtFechaFiltro)
        edNombreFiltro = findViewById(R.id.edtBuscarNombreUsu)
        edFechaFiltro.inputType = InputType.TYPE_NULL
        imgBorrarFecha.setOnClickListener{
            edFechaFiltro.setText("")
        }
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this@DatosEmpleados,
            {
                _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                edFechaFiltro.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        edFechaFiltro.setOnClickListener{
            datePickerDialog.show()
        }

        cargarCargos()
        obtenerEmpleados()
    }
    fun filtrar(){
        val idCargo = spCargos.selectedItemId
        val nombre = edNombreFiltro.text
        val fecha = edFechaFiltro.text
        lifecycleScope.launch(Dispatchers.IO){
            var datos = empleadoDao.obtenerTodo()
            var datosFiltrados : List<EmpleadoUsuarioYCargo> = datos
            if(idCargo.toInt() != 0){
                datosFiltrados = datosFiltrados.filter { empleado -> empleado.empleado.cargo.id == idCargo}
            }

            if(!fecha.isNullOrEmpty()){
                datosFiltrados = datosFiltrados.filter { empleado -> empleado.empleado.empleado.fechaRegistro.trim().contains(fecha.trim())  }
            }
            if (!nombre.isNullOrEmpty() && nombre.matches(Regex("^[a-zA-Z ]+\$"))) {
                datosFiltrados = datosFiltrados.filter { empleado -> empleado.empleado.empleado.nombreEmpleado.contains(nombre, ignoreCase = true) }
            } else if(!nombre.isNullOrEmpty()){
                // El campo de nombre contiene caracteres no permitidos
                mostrarToast("Ingrese solo texto en el nombre")
            }
            withContext(Dispatchers.Main){
                adaptador.actualizarListaEmpleados(datosFiltrados)
            }
        }

    }

    fun cargarCargos(){
        lifecycleScope.launch(Dispatchers.IO) {
            val cargos = cargoDao.obtenerTodo()
            val nombresCargos = cargos.map { it.cargo }

            val opciones = mutableListOf<String>()
            opciones.add("Seleccionar cargo")
            opciones.addAll(nombresCargos)

            val adapter = ArrayAdapter(this@DatosEmpleados, android.R.layout.simple_spinner_item, opciones)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spCargos.adapter = adapter
        }
    }
    fun obtenerEmpleados() {
        lifecycleScope.launch(Dispatchers.IO) {
            val datos = empleadoDao.obtenerTodoLiveData()
            withContext(Dispatchers.Main) {
                datos.observe(this@DatosEmpleados) { empleados ->
                    if(empleados.size == 0){
                        tvEtiqueta.visibility = View.VISIBLE
                    }else{
                        tvEtiqueta.visibility = View.GONE
                    }
                    adaptador = EmpleadoAdapter(empleados)
                    rvEmpleados.layoutManager = LinearLayoutManager(this@DatosEmpleados)
                    rvEmpleados.adapter = adaptador

                }
            }
        }
    }
    fun volver(){
        var intent = Intent(this, ConfiguracionVista::class.java)
        startActivity(intent)
    }
    fun agregar(){
        var intent = Intent(this, NuevoEmpleado::class.java)
        startActivity(intent)
    }
    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}