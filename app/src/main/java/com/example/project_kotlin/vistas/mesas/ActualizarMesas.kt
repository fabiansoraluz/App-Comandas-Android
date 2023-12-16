package com.example.project_kotlin.vistas.mesas

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
import com.example.project_kotlin.dao.ComandaDao
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
import retrofit2.Callback
import retrofit2.Response

class ActualizarMesas : AppCompatActivity() {

    private lateinit var edCantAsientos: EditText
    private lateinit var edNumMesa: EditText
    private lateinit var btnEditar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnVolver: Button
    private lateinit var mesaDao: MesaDao
    private lateinit var comandaDao: ComandaDao
    private lateinit var mesaBean : Mesa
    private lateinit var apiMesa : ApiServiceMesa
    private lateinit var bd: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_mesa)
        mesaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).mesaDao()
        comandaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comandaDao()
        apiMesa = ApiUtils.getAPIServiceMesa()
        edCantAsientos = findViewById(R.id.edtApeUsuE)
        edNumMesa = findViewById(R.id.edtNumMesa)
        btnEditar = findViewById(R.id.btnEditarCategoria)
        btnEliminar = findViewById(R.id.btnEliminarCat)
        btnVolver = findViewById(R.id.btnCancelarCat)

        btnVolver.setOnClickListener { Volver() }
        btnEliminar.setOnClickListener { Eliminar() }
        btnEditar.setOnClickListener { Editar() }
        conectar()
        //Cargar dato
        mesaBean = intent.getSerializableExtra("mesa") as Mesa
        edNumMesa.setText(mesaBean.id.toString())
        edCantAsientos.setText(mesaBean.cantidadAsientos.toString())
    }
    fun conectar(){
        FirebaseApp.initializeApp(this)
        bd= FirebaseDatabase.getInstance().reference
    }
    fun Volver() {
        val intent = Intent(this, DatosMesas::class.java)
        startActivity(intent)
    }

    fun Eliminar() {
        val numMesa = edNumMesa.text.toString().toInt()
        val mensaje: AlertDialog.Builder = AlertDialog.Builder(this)
        mensaje.setTitle("Sistema comandas")
        mensaje.setMessage("¿Seguro de eliminar?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                //Validar de comandas
                val validarComandaPorMesa = comandaDao.obtenerComandasPorMesa(numMesa)
                if (validarComandaPorMesa.isEmpty()) {
                    mesaDao.eliminar(mesaBean)
                    eliminarMysql(mesaBean.id)
                    bd.child("mesa").child(mesaBean.id.toString()).removeValue()
                } else {
                    mostrarToast("No puedes eliminar mesas que tienen información de comandas")
                }
            }
        }
        mensaje.setNegativeButton("Cancelar") { _, _ -> }
        mensaje.setIcon(android.R.drawable.ic_delete)
        mensaje.show()
    }
    fun eliminarMysql(id:Long){
        apiMesa.fetcEliminarMesa(id.toInt()).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Mesa eliminada correctamente")
                Volver()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }


    //HOLA
    fun Editar() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (mesaBean.estado == "Libre") {
                if (validarCampos()) {
                    val cantidadAsientos = edCantAsientos.text.toString().toInt()
                    mesaBean.cantidadAsientos = cantidadAsientos
                    mesaDao.actualizar(mesaBean)
                    actualizarMesaMysql(mesaBean)
                    bd.child("mesa").child(mesaBean.id.toString()).setValue(MesaNoSql(mesaBean.cantidadAsientos, mesaBean.estado))
                }
            } else {
                mostrarToast("No puedes actualizar una mesa ocupada")
            }
        }
    }
    fun actualizarMesaMysql(bean:Mesa){
        apiMesa.fetchActualizarMesa(bean).enqueue(object:Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Mesa actualizada correctamente")
                Volver()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }

    fun validarCampos(): Boolean {
        val cantidad = edCantAsientos.text.toString().toIntOrNull()
        if (cantidad == null || cantidad !in 1..9) {
            mostrarToast("La cantidad de asientos debe ser un número del 1 al 9")
            return false
        }
        return true
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}