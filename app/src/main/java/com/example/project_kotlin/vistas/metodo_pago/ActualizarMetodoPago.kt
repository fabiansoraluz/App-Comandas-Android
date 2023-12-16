package com.example.project_kotlin.vistas.metodo_pago

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.ComprobanteDao
import com.example.project_kotlin.dao.MetodoPagoDao
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

class ActualizarMetodoPago : AppCompatActivity() {

    private lateinit var edtNomMetodoPago: EditText
    private lateinit var btnEditarPago: Button
    private lateinit var btnEliminarPago: Button
    private lateinit var btnVolverListadoPago: Button
    private lateinit var metodoPagoDao: MetodoPagoDao
    private lateinit var comprobanteDao: ComprobanteDao

    lateinit var  bd: DatabaseReference

    private lateinit var metodoPagoBean : MetodoPago

    private lateinit var apiMetodoPago: ApiServiceMetodoPago

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_pago)
        metodoPagoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).metodoPagoDao()

        conectarApi()

        edtNomMetodoPago = findViewById(R.id.edtNomMetodoPago)
        btnEditarPago = findViewById(R.id.btnEditarPago)
        btnEliminarPago = findViewById(R.id.btnEliminarPago)
        btnVolverListadoPago = findViewById(R.id.btnVolverListadoPago)

        btnVolverListadoPago.setOnClickListener { volver() }
        btnEliminarPago.setOnClickListener { eliminar() }
        btnEditarPago.setOnClickListener { editar() }

        apiMetodoPago = ApiUtils.getAPIServiceMetodoPago()

        comprobanteDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comprobanteDao()

        //cargar datos
        metodoPagoBean = intent.getSerializableExtra("metodo_pago") as MetodoPago
        edtNomMetodoPago.setText(metodoPagoBean.nombreMetodoPago)

    }

    fun volver() {
        val intent = Intent(this, DatosMetodoPago::class.java)
        startActivity(intent)
    }

    fun eliminar() {

        val mensaje: AlertDialog.Builder = AlertDialog.Builder(this)
        mensaje.setTitle("Sistema de Pagos")
        mensaje.setMessage("¿Seguro de eliminar?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {

                //Validar por eliminacion de Comprobante
                val validarMetodo=comprobanteDao.obtenerComprobantePorMetodoPago(metodoPagoBean.id.toInt())
                if(validarMetodo.isEmpty()) {

                    metodoPagoDao.eliminar(metodoPagoBean)
                    EliminarMySql(metodoPagoBean)

                    bd.child("metodopago").child(metodoPagoBean.id.toString()).removeValue()

                    mostrarToast("Método de pago eliminado correctamente")
                    volver()
                }else{
                    mostrarToast("No puedes eliminar un Metodo de pago que tienen información en Comprobante")
                }
            }
        }
        mensaje.setNegativeButton("Cancelar") { _, _ -> }
        mensaje.setIcon(android.R.drawable.ic_delete)
        mensaje.show()
    }

    fun EliminarMySql(bean: MetodoPago) {
        apiMetodoPago.fetcEliminarMetodoPago(bean.id).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Método de pago eliminado correctamente")
                volver()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }

    fun validarCampos(): Boolean {
        val nombre = edtNomMetodoPago.text.toString()
        val regex = Regex("^(?=.{3,100}\$)[A-Za-zÑÁÉÍÓÚñáéíóú][A-Za-zÑÁÉÍÓÚñáéíóú]+(?: [A-Za-zÑÁÉÍÓÚñáéíóú]+)*\$")  // Expresión regular que verifica si solo hay letras

        if (nombre.isBlank()) {
            mostrarToast("El campo de nombre no puede estar vacío")
            return false
        }

        if (!regex.matches(nombre)) {
            mostrarToast("El campo de nombre solo puede contener letras")
            return false
        }


        return true
    }

    @SuppressLint("SuspiciousIndentation")
    fun editar() {
        val nuevoNombre = edtNomMetodoPago.text.toString().trim()

        lifecycleScope.launch(Dispatchers.IO) {
            if (validarCampos()) {
                val listarMetodoPago = metodoPagoDao.obtenerTodo()
                for (pago in listarMetodoPago) {
                    val validamet = pago.nombreMetodoPago.trim().lowercase()
                    if (nuevoNombre.lowercase() == validamet && pago.id != metodoPagoBean.id)  {
                        mostrarToast("El método de pago ya existe")
                        return@launch
                    }
                }

                metodoPagoBean.nombreMetodoPago = nuevoNombre
                    EditarMySql(metodoPagoBean)
                    metodoPagoDao.actualizar(metodoPagoBean)



                    val beanNoSql= MetodoPagoNoSql(metodoPagoBean.nombreMetodoPago)
                    bd.child("metodopago").child(metodoPagoBean.id.toString()).setValue(beanNoSql)

                    mostrarToast("Método de pago actualizado correctamente")
                    volver()
            }
        }
    }

    fun EditarMySql(bean: MetodoPago) {
        apiMetodoPago.fetchActualizarMetodoPago(bean).enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Método de pago actualizado correctamente")
                volver()
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

    fun conectarApi() {
        //inicar mi firebase
        FirebaseApp.initializeApp(this)
        bd= FirebaseDatabase.getInstance().reference
    }
}


