package com.example.project_kotlin.vistas.mesas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import retrofit2.Callback
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.MesaDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Mesa
import com.example.project_kotlin.entidades.firebase.MesaNoSql
import com.example.project_kotlin.service.ApiServiceMesa
import com.example.project_kotlin.utils.ApiUtils
import com.example.project_kotlin.utils.appConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class NuevaMesa : AppCompatActivity() {

    private lateinit var btnVolverListadoMesa: Button
    private lateinit var btnAgregar: Button
    private lateinit var edCantidadAsientos: EditText
    private lateinit var mesaDao: MesaDao
    private lateinit var apiMesa : ApiServiceMesa

    //Variable para acceder a la base de datos creada en el proyecto en firebase
    lateinit var bdFirebase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_mesa)

        apiMesa = ApiUtils.getAPIServiceMesa()
        btnVolverListadoMesa = findViewById(R.id.btnCancelarAgregarMesa)
        mesaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).mesaDao()
        btnAgregar = findViewById(R.id.btnNuevaMesaA)
        edCantidadAsientos = findViewById(R.id.edtCanAsientosMesaA)
        btnVolverListadoMesa.setOnClickListener { volver() }
        btnAgregar.setOnClickListener { agregarMesa() }
        conectar()
    }

    fun agregarMesa() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (validarCampos()) {
                val cantidad = edCantidadAsientos.text.toString().toInt()
                val bean = Mesa(cantidadAsientos = cantidad, estado = "Libre")
                val mesaId = mesaDao.guardar(bean)
                grabarMesaMysql(bean)
                //GENERAR UN ID "HASH" para firebase

                //CREAR NODO RAIZ y nodo de tipo mesa
                //Los doble signo de exclamación significa que estamos seguros que no será nulo
                val beanNoSql = MesaNoSql(bean.cantidadAsientos, bean.estado)
                bdFirebase.child("Mesa").child(mesaId.toString()).setValue(beanNoSql)
                mostrarToast("Mesa agregada correctamente")
                volver()
            }
        }
    }
    fun grabarMesaMysql(bean:Mesa){
        apiMesa.fetchGuardarMesa(bean).enqueue(object:Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }
    //Ejemplo de cómo pueden validar sus campos en Kotlin
    fun validarCampos(): Boolean {
        val cantidad = edCantidadAsientos.text.toString().toIntOrNull()
        if (cantidad == null || cantidad !in 1..9) {
            mostrarToast("La cantidad de asientos debe ser un número de 1 al 9")
            return false
        }
        return true
    }

    fun volver() {
        val intent = Intent(this, DatosMesas::class.java)
        startActivity(intent)
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