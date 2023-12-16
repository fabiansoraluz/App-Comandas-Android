package com.example.project_kotlin.vistas.caja_registradora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button

import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.example.project_kotlin.R
import com.example.project_kotlin.dao.CajaDao
import com.example.project_kotlin.dao.EstablecimientoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Caja
import com.example.project_kotlin.entidades.firebase.CajaNoSql
import com.example.project_kotlin.entidades.firebase.EstablecimientoNoSql
import com.example.project_kotlin.service.ApiServiceCaja
import com.example.project_kotlin.utils.ApiUtils


import com.example.project_kotlin.utils.appConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import kotlin.collections.map


class NuevaCaja : AppCompatActivity() {

    private lateinit var btnRegresar: Button
    private lateinit var btnRegistrarCaja: Button
    private lateinit var spnEstablecimiento: Spinner
    private lateinit var cajaDao: CajaDao
    private lateinit var establecimientoDao: EstablecimientoDao

    private var establecimientoDefault : String = "Seleccionar Establecimiento"

    private lateinit var apiCaja: ApiServiceCaja
    lateinit var bd: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_caja)
        conectar()

        btnRegresar = findViewById(R.id.btnRegresarlistaCajas)
        cajaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cajaDao()
        btnRegistrarCaja = findViewById(R.id.btnAgregarCaja)
        spnEstablecimiento = findViewById(R.id.spnEstablecimiento)

        btnRegresar.setOnClickListener { volver() }
        btnRegistrarCaja.setOnClickListener { agregarCaja() }

        establecimientoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).establecimientoDao()
        apiCaja = ApiUtils.getAPIServiceCaja()

        cargarEstablecimiento()
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
                this@NuevaCaja,
                android.R.layout.simple_spinner_item,opciones
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Asigna el adaptador al Spinner
            spnEstablecimiento.adapter = adapter
        }
    }




    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(this@NuevaCaja, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun volver() {
        val intent = Intent(this, DatosCajas::class.java)
        startActivity(intent)
    }


    private fun agregarCaja() {
        lifecycleScope.launch(Dispatchers.IO) {



            val establecimientoId = spnEstablecimiento.selectedItemPosition
            if (establecimientoId == 0) {
                mostrarToast("Seleccione un establecimiento")
            } else {
                val establecimiento = establecimientoDao.obtenerPorId(establecimientoId.toLong())
                val listaCaja: List<Caja> = cajaDao.obtenerTodo()

                val nuevaCaja = Caja(listaCaja)
                nuevaCaja.establecimiento = establecimiento

                cajaDao.guardar(nuevaCaja)

                agregarCajaMySql(nuevaCaja)

                // Guardar en Firebase
                val establecimientoNoSql = EstablecimientoNoSql(
                    establecimiento.nomEstablecimiento,
                    establecimiento.telefonoestablecimiento,
                    establecimiento.direccionestablecimiento,
                    establecimiento.rucestablecimiento
                )
                val cajaNoSql = CajaNoSql(establecimientoNoSql)

                bd.child("caja").child(nuevaCaja.id.toString()).setValue(cajaNoSql)

                mostrarToast("Caja registrada")
                volver()
            }
        }
    }

    fun agregarCajaMySql(bean: Caja) {
        Log.d("Datos de la caja:", "ID: ${bean.id}, Establecimiento: ${bean.establecimiento?.nomEstablecimiento}")

        apiCaja.fetchGuardarCaja(bean).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("Respuesta del servidor:", response.message())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error al enviar la caja:", t.toString())
            }
        })
    }


    fun conectar(){

        //inicar mi firebase
        FirebaseApp.initializeApp(this)
        bd= FirebaseDatabase.getInstance().reference
    }
    //
}


