package com.example.project_kotlin.vistas.caja_registradora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.CajaDao
import com.example.project_kotlin.dao.ComprobanteDao
import com.example.project_kotlin.dao.EstablecimientoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Caja
import com.example.project_kotlin.entidades.Establecimiento
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


class ActualizarCajas  : AppCompatActivity(){


    private lateinit var btnVolverListadoCaja: Button
    private lateinit var btnEditarCaja: Button
    private lateinit var btnEliminarCaja: Button
    private lateinit var edtCodigoCajaEdit: EditText
    private lateinit var spnEstablecimientoEdit: Spinner
    private lateinit var CajaDao: CajaDao
    private lateinit var ComprobanteDao: ComprobanteDao
    private lateinit var establecimientoDao: EstablecimientoDao
    private lateinit var cajabean: Caja

    private lateinit var apiCaja: ApiServiceCaja

    lateinit var bd: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_caja)
        conectar()
        btnVolverListadoCaja = findViewById(R.id.btnVolverListadoCaja)
        edtCodigoCajaEdit = findViewById(R.id.edtCodigoCajaEdit)
        CajaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cajaDao()
        ComprobanteDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comprobanteDao()
        btnEditarCaja = findViewById(R.id.btnEditarCaja)
        btnEliminarCaja = findViewById(R.id.btnEliminarCaja)
        spnEstablecimientoEdit = findViewById(R.id.spnEstablecimientoEdit)

        btnVolverListadoCaja.setOnClickListener { volver() }
        btnEditarCaja.setOnClickListener { actualizarCajas() }
        btnEliminarCaja.setOnClickListener { Eliminar()}
        establecimientoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).establecimientoDao()

        apiCaja = ApiUtils.getAPIServiceCaja()

        cajabean = intent.getSerializableExtra("caja") as Caja

        cargarEstablecimiento()
        edtCodigoCajaEdit.setText(cajabean.id.toString())

    }



    private fun cargarEstablecimiento() {
        lifecycleScope.launch(Dispatchers.IO) {
            val establecimientoList = establecimientoDao.obtener()
            val nombreEstablecimientoList = establecimientoList.map { it.nomEstablecimiento }

            val opciones = mutableListOf<String>()
            opciones.add("Seleccionar Establecimiento")
            opciones.addAll(nombreEstablecimientoList)

            val adapter = ArrayAdapter(
                this@ActualizarCajas,
                android.R.layout.simple_spinner_item,
                opciones
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spnEstablecimientoEdit.adapter = adapter

            val seleccion = establecimientoList.indexOfFirst { it.id == cajabean.establecimiento?.id } + 1
            spnEstablecimientoEdit.setSelection(seleccion)
        }
    }


    private fun obtenerIdCajaExistente(): String {
        val cajaExistente: Caja? = CajaDao.obtenerPorId(cajabean.id)
        return cajaExistente?.id ?: ""
    }

    fun Establecimiento.toEstablecimientoNoSql(): EstablecimientoNoSql {
        return EstablecimientoNoSql(
            this.nomEstablecimiento,
            this.telefonoestablecimiento,
            this.direccionestablecimiento,
            this.rucestablecimiento
        )
    }



    fun actualizarCajas() {
        lifecycleScope.launch(Dispatchers.IO) {
            val establecimientoId = spnEstablecimientoEdit.selectedItemPosition
            if (establecimientoId == 0) {
                mostrarToast("Seleccione un establecimiento")
            } else {
                val codigo = edtCodigoCajaEdit.text.toString()
                val establecimiento = establecimientoDao.obtenerPorId(establecimientoId.toLong())

                cajabean.id= codigo
                cajabean.establecimiento = establecimiento
                CajaDao.actualizar(cajabean)
                EditarMysql(cajabean)

                val beanNoSql =
                    CajaNoSql(establecimiento!!.toEstablecimientoNoSql())
                bd.child("caja").child(cajabean.id).setValue(beanNoSql)

                mostrarToast("Caja actualizada correctamente")
                volver()

            }
        }
    }



    fun EditarMysql(bean: Caja){
        apiCaja.fetchActualizarCaja(bean).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Caja actualizado correctamente")
                volver()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }



    fun Eliminar() {
        val CodCaja = edtCodigoCajaEdit.text.toString()
        val mensaje: AlertDialog.Builder = AlertDialog.Builder(this)
        mensaje.setTitle("Sistema comandas")
        mensaje.setMessage("¿Seguro de eliminar?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                val validarComprobante = ComprobanteDao.ComprobanteCaja(CodCaja)
                if(validarComprobante.isEmpty()){
                    // Eliminar en la base de datos local
                    CajaDao.eliminar(cajabean)
                    // Eliminar en el servidor MySQL
                    EliminarMySql(CodCaja)
                    bd.child("caja").child(cajabean.id).removeValue()
                    mostrarToast("Caja eliminada")
                    volver()
                }else{
                    mostrarToast("No puedes eliminar una Caja que tiene informacion en comprobante")
                }

            }
        }
        mensaje.setNegativeButton("Cancelar") { _, _ -> }
        mensaje.setIcon(android.R.drawable.ic_delete)
        mensaje.show()
    }

    fun EliminarMySql(codigo: String){
        apiCaja.fetcEliminarCaja(codigo).enqueue(object:Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Caja eliminada correctamente")
                volver()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }


    fun volver() {
        val intent = Intent(this, DatosCajas::class.java)
        startActivity(intent)
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    fun conectar(){
        //inicar mi firebase
        FirebaseApp.initializeApp(this)
        bd= FirebaseDatabase.getInstance().reference
    }



}