package com.example.project_kotlin.vistas.caja_registradora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.cajas.ConfiguracionCajasAdapter
import com.example.project_kotlin.adaptador.adaptadores.mesas.ConfiguracionMesasAdapter
import com.example.project_kotlin.dao.CajaDao
import com.example.project_kotlin.dao.EstablecimientoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Caja
import com.example.project_kotlin.entidades.Plato
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.ConfiguracionVista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DatosCajas : AppCompatActivity()  {

    private lateinit var rvCajas : RecyclerView
    private lateinit var btnNuevaCaja : Button
    private lateinit var btnVolverIndexCaja : Button
    private lateinit var spnFiltrarCajas : Spinner
    private lateinit var btnFiltrarCajaPest : Button
    private lateinit var tvSinRegistrosCaja : TextView

    private lateinit var establecimientoDao: EstablecimientoDao

    private var establecimientoDefault : String = "Seleccionar Establecimiento"

    private lateinit var CajaDao : CajaDao
    private lateinit var adaptador : ConfiguracionCajasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.con_caja)
        btnNuevaCaja = findViewById(R.id.btnNuevaCaja)
        btnVolverIndexCaja = findViewById(R.id.btnVolverIndexCaja)
        rvCajas = findViewById(R.id.rvCajas)
        spnFiltrarCajas = findViewById(R.id.spnEstablecimientoFlitrado)
        btnFiltrarCajaPest = findViewById(R.id.btnFiltroEstCaja)
        tvSinRegistrosCaja = findViewById(R.id.tvSinRegistrosCaja)

        CajaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cajaDao()
        establecimientoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).establecimientoDao()

        btnNuevaCaja.setOnClickListener({ adicionar() })
        btnVolverIndexCaja.setOnClickListener({ volverIndex() })


        btnFiltrarCajaPest.setOnClickListener({filtrar()})

        obtenerCaja()
        cargarEstablecimiento()
    }
    
    fun filtrar() {
        lifecycleScope.launch(Dispatchers.IO) {
            val datos = CajaDao.obtenerTodo()
            var datosFiltrados: List<Caja> = datos

            val selectedItem = spnFiltrarCajas.selectedItem.toString()
            establecimientoDefault = selectedItem

            if (selectedItem != "Seleccionar Establecimiento") {
                datosFiltrados = datosFiltrados.filter { caja -> caja.establecimiento?.nomEstablecimiento == selectedItem }
            }
            withContext(Dispatchers.Main) {
                if (datosFiltrados.isNotEmpty()) {
                    adaptador.actualizarListaCajas(datosFiltrados)
                } else {

                    mostrarToast("No se encontraron registros")
                }
            }
        }
    }




    private fun cargarEstablecimiento() {
        lifecycleScope.launch(Dispatchers.IO) {

            // Obtén la lista de categorías de platos desde la base de datos
            var data = establecimientoDao.obtener()

            var nombreEstablecimientoList =   data.map {  it.nomEstablecimiento }

            val opciones = mutableListOf<String>()
            opciones.add("Seleccionar Establecimiento")
            opciones.addAll(nombreEstablecimientoList)

            // Crea un ArrayAdapter con los nombres de las categorías de establecimientos
            var adapter = ArrayAdapter(
                this@DatosCajas,
                android.R.layout.simple_spinner_item,opciones
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Asigna el adaptador al Spinner
            spnFiltrarCajas.adapter = adapter
        }
    }

    fun volverIndex(){
        var intent = Intent(this, ConfiguracionVista::class.java)
        startActivity(intent)
    }
    fun obtenerCaja() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val datos = CajaDao.obtenerTodo()

                withContext(Dispatchers.Main) {
                    if (datos.isNotEmpty()) {
                        adaptador = ConfiguracionCajasAdapter(datos)
                        rvCajas.layoutManager = LinearLayoutManager(this@DatosCajas)
                        rvCajas.adapter = adaptador


                        tvSinRegistrosCaja.visibility = View.GONE
                    } else {
                        tvSinRegistrosCaja.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                val mensajeError = "Error al obtener los datos: ${e.message}"
                mostrarToast(mensajeError)
            }
        }
    }



    fun adicionar(){
        var intent = Intent(this, NuevaCaja::class.java)
        startActivity(intent)
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

}