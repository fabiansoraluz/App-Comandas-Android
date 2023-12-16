package com.example.project_kotlin.vistas.metodo_pago

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.dao.MetodoPagoDao
import com.example.project_kotlin.R
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.MetodoPago
import com.example.project_kotlin.entidades.firebase.MetodoPagoNoSql
import com.example.project_kotlin.service.ApiServiceMetodoPago
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

class NuevoMetodoPago:AppCompatActivity() {

    private lateinit var edtNomPago: EditText
    private lateinit var btnAgregarPago: Button
    private lateinit var btnVolverListadoPago: Button
    private lateinit var metodoPagoDao: MetodoPagoDao

    private lateinit var apiMetodoPago: ApiServiceMetodoPago

    lateinit var  bd:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_pago)
        conectarApi()

        btnVolverListadoPago = findViewById(R.id.btnCancelarPago)
        metodoPagoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).metodoPagoDao()
        btnAgregarPago = findViewById(R.id.btnAgregarMetPago)
        edtNomPago = findViewById(R.id.edtNomPago)

        //llamamos al api
        apiMetodoPago =  ApiUtils.getAPIServiceMetodoPago()

        btnVolverListadoPago.setOnClickListener { volver() }
        btnAgregarPago.setOnClickListener { agregarPago() }



    }


    fun agregarPago() {
        lifecycleScope.launch(Dispatchers.IO){
            if (validarCampos()){
                val nombre = edtNomPago.text.toString().trim()
                val listarMetodoPago = metodoPagoDao.obtenerTodo()

                for (pago in listarMetodoPago) {
                    val validamet = pago.nombreMetodoPago.trim().lowercase()
                    if (nombre.lowercase() == validamet) {
                        mostrarToast("El método de pago ya existe")
                        return@launch
                    }
                }


                val bean = MetodoPago(nombreMetodoPago = nombre)
                val metodoPagoId = metodoPagoDao.registrar(bean)

                agregarMetodoPagoMySql(bean)

                val beanNoSql = MetodoPagoNoSql(bean.nombreMetodoPago)
                bd.child("metodopago").child(metodoPagoId.toString()).setValue(beanNoSql)

                mostrarToast("Metodo de Pago agregado correctamente")
                volver()
            }
        }
    }

    fun agregarMetodoPagoMySql(bean: MetodoPago) {
        apiMetodoPago.fetchGuardarMetodoPago(bean).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarCampos(): Boolean {
        val nombre = edtNomPago.text.toString()
        val regex = Regex("^(?=.{3,100}\$)[A-Za-zÑÁÉÍÓÚñáéíóú][A-Za-zÑÁÉÍÓÚñáéíóú]+(?: [A-Za-zÑÁÉÍÓÚñáéíóú]+)*\$")  // Expresión regular que verifica si solo hay letras

        if (!regex.matches(nombre)) {
            mostrarToast("El campo de nombre solo puede contener letras")
            return false
        }

        if (nombre.isBlank()) {
            mostrarToast("El campo de nombre no puede estar vacío")
            return false
        }

        return true
    }

    private fun volver() {
        val intent = Intent(this, DatosMetodoPago::class.java)
        startActivity(intent)
    }

    fun conectarApi() {
        //inicar mi firebase
        FirebaseApp.initializeApp(this)
        bd= FirebaseDatabase.getInstance().reference
    }

}