package com.example.project_kotlin.vistas.establecimiento

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
import com.example.project_kotlin.dao.CajaDao
import com.example.project_kotlin.dao.EstablecimientoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Establecimiento
import com.example.project_kotlin.entidades.firebase.EstablecimientoNoSql
import com.example.project_kotlin.service.ApiServiceEstablecimiento
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

class ActualizarEstablecimiento:AppCompatActivity() {
    private lateinit var edtCod:EditText
    private lateinit var edtNombre:EditText
    private lateinit var edtDireccion:EditText
    private lateinit var edtRuc:EditText
    private lateinit var edtTelefono:EditText
    private lateinit var btnActualizar:Button
    private lateinit var btnEliminar:Button
    private lateinit var btnVolver:Button
    private lateinit var cajaDao:CajaDao
    private lateinit var establecimientoDao:EstablecimientoDao


    private lateinit var establecimientoBean:Establecimiento

    private lateinit var apiEstablecimiento: ApiServiceEstablecimiento

    lateinit var bd: DatabaseReference

    private  val REGEX_NOMBRE = "^(?=.{3,100}$)[A-ZÑÁÉÍÓÚ][A-ZÑÁÉÍÓÚa-zñáéíóú]+(?: [A-ZÑÁÉÍÓÚa-zñáéíóú]+)*$"
    private  val REGEX_DIRECCION = "^(?=.{3,100}$)[A-ZÑÁÉÍÓÚ][A-Za-zñáéíóú0-9.\\-]+(?: [A-Za-zñáéíóú0-9.\\-]+)*$"
    private  val REGEX_TELEFONO = "^9[0-9]{8}$"
    private  val REGEX_RUC = "^[0-9]{11}$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modificar_establecimiento)
        establecimientoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).establecimientoDao()
        cajaDao=ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cajaDao()
        conectar()
        edtCod=findViewById(R.id.edtNomUsuE)
        edtNombre = findViewById(R.id.edtDireccion)
        edtDireccion = findViewById(R.id.edtDniUsuE)
        edtRuc = findViewById(R.id.edtCorreoUsuE)
        edtTelefono = findViewById(R.id.edtTelefonoA)

        btnActualizar = findViewById(R.id.btnNuevoEstablecimiento)
        btnEliminar = findViewById(R.id.btnEliminarEstablecimiento)
        btnVolver = findViewById(R.id.btnEliminarUsu)

        btnVolver.setOnClickListener { Volver() }
        btnEliminar.setOnClickListener { Eliminar() }
        btnActualizar.setOnClickListener { Editar() }
        apiEstablecimiento=ApiUtils.getAPIServiceEstablecimiento()

        //Cargar dato
       establecimientoBean = intent.getSerializableExtra("establecimiento") as Establecimiento
        edtCod.setText(establecimientoBean.id.toString())
        edtNombre.setText(establecimientoBean.nomEstablecimiento)
        edtDireccion.setText(establecimientoBean.direccionestablecimiento)
        edtRuc.setText(establecimientoBean.rucestablecimiento)
        edtTelefono.setText(establecimientoBean.telefonoestablecimiento)

    }

    fun Editar() {
        lifecycleScope.launch(Dispatchers.IO) {
                if (validarCampos()) {
                    val nombre = edtNombre.text.toString()
                    val direccion = edtDireccion.text.toString()
                    val telefono = edtTelefono.text.toString()
                    val ruc = edtRuc.text.toString();
                    establecimientoBean.nomEstablecimiento = nombre
                    establecimientoBean.direccionestablecimiento = direccion
                    establecimientoBean.rucestablecimiento = ruc
                    establecimientoBean.telefonoestablecimiento = telefono
                    establecimientoDao.actualizar(establecimientoBean)
                    EditarMysql(establecimientoBean)

                    val beanNoSql = EstablecimientoNoSql(establecimientoBean.nomEstablecimiento,establecimientoBean.telefonoestablecimiento,
                        establecimientoBean.direccionestablecimiento,establecimientoBean.rucestablecimiento)
                    bd.child("establecimiento").child(establecimientoBean.id.toString()).setValue(beanNoSql)
                    mostrarToast("Establecimiento actualizado correctamente")
                    Volver()
                }
        }
        }

        fun EditarMysql(bean:Establecimiento){
            apiEstablecimiento.fetchUpdateEstablecimiento(bean).enqueue(object :Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    mostrarToast("Establecimiento actualizada correctamente")
                    Volver()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("Error : ",t.toString())
                }
            })
        }

    fun Eliminar() {
        val numEstablecimiento = edtCod.text.toString().toInt()
        val mensaje: AlertDialog.Builder = AlertDialog.Builder(this)
        mensaje.setTitle("Sistema comandas")
        mensaje.setMessage("¿Seguro de eliminar?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                    //Validar caja
                    val validarCaja=cajaDao.obtenerCajaPorEstablecimiento(numEstablecimiento)
                    if(validarCaja.isEmpty()){
                        establecimientoDao.eliminar(establecimientoBean)
                        EliminarMySql(numEstablecimiento.toLong())
                        bd.child("establecimiento").child(establecimientoBean.id.toString()).removeValue()
                        mostrarToast("Establecimiento eliminado correctamente")
                        Volver()

                    }else{
                        mostrarToast("No puedes eliminar Establecimiento que tienen información en Caja")
                    }

                }
            }

        mensaje.setNegativeButton("Cancelar") { _, _ -> }
        mensaje.setIcon(android.R.drawable.ic_delete)
        mensaje.show()
    }

    fun EliminarMySql(codigo: Long){
        apiEstablecimiento.fetcEliminarEstablecimiento(codigo.toInt()).enqueue(object:Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                mostrarToast("Establecimiento eliminada correctamente")
                Volver()
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }




    fun validarCampos():Boolean{

        val nombre=edtNombre.text
        val direccion=edtDireccion.text
        val telefono=edtTelefono.text
        val ruc=edtRuc.text

        if (!REGEX_NOMBRE.toRegex().matches(nombre)) {
            // El campo nombre no cumple con el formato esperado

            mostrarToast("El campo nombre no cumple con el formato requerido. Debe comenzar con mayúscula y contener solo letras y espacios")
            return false
        }
        if(!REGEX_DIRECCION.toRegex().matches(direccion)) {
            mostrarToast("La dirección ingresada no cumple con el formato requerido. Debe comenzar con mayúscula. Ejemplo: Av. Principal 123-A.")
            return false
        }

        if(!REGEX_RUC.toRegex().matches(ruc)){
            mostrarToast("El RUC es incorrecto. Debe tener 11 dígitos numéricos")
            return false
        }
        if(!REGEX_TELEFONO.toRegex().matches(telefono)){
            mostrarToast("Introduce un número válido. Solo acepta 9 dígitos y comienza con 9")
            return false
        }

        return true

    }

    fun Volver() {
        val intent = Intent(this, DatosEstablecimiento::class.java)
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

