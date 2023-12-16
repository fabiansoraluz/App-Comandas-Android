package com.example.project_kotlin.vistas.establecimiento

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
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

class NuevoEstablecimiento:AppCompatActivity() {

        private  lateinit var edtNombre:EditText
        private lateinit var edtDireccion:EditText
        private lateinit var edtRuc:EditText
        private lateinit var edtTelefono:EditText
        private lateinit var establecimientoDao:EstablecimientoDao
        private lateinit var btnaAgregarEstablecimiento:Button
        private lateinit var btnVolverListadoEstablecimiento:Button

        private lateinit var apiEstablecimiento: ApiServiceEstablecimiento

        //variable para acceder a la base de datos creada en el proyecto con firebase
        lateinit var bd:DatabaseReference

        private  val REGEX_NOMBRE = "^(?=.{3,100}$)[A-ZÑÁÉÍÓÚ][A-ZÑÁÉÍÓÚa-zñáéíóú]+(?: [A-ZÑÁÉÍÓÚa-zñáéíóú]+)*$"
        private  val REGEX_DIRECCION = "^(?=.{3,100}$)[A-ZÑÁÉÍÓÚ][A-Za-zñáéíóú0-9.\\-]+(?: [A-Za-zñáéíóú0-9.\\-]+)*$"
        private  val REGEX_TELEFONO = "^9[0-9]{8}$"
        private  val REGEX_RUC = "^[0-9]{11}$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.agregar_establecimiento)
        conectar()

        edtNombre = findViewById(R.id.edtNomUsuE)
        edtDireccion = findViewById(R.id.edtDireccion)
        edtRuc = findViewById(R.id.edtDniUsuE)
        edtTelefono = findViewById(R.id.edtCorreoUsuE)
        btnVolverListadoEstablecimiento = findViewById(R.id.btnEliminarUsu)
        establecimientoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).establecimientoDao()
        btnaAgregarEstablecimiento = findViewById(R.id.btnNuevoEstablecimiento)

        //Hacemos el llamado del apiEstablecimiento
        apiEstablecimiento=ApiUtils.getAPIServiceEstablecimiento()
       btnVolverListadoEstablecimiento.setOnClickListener { volver() }
        btnaAgregarEstablecimiento.setOnClickListener { agregarEstablecimiento() }

    }

    fun agregarEstablecimiento(){
        lifecycleScope.launch(Dispatchers.IO) {
            if(validarCampos()){
                val nombre=edtNombre.text.toString()
                val direccion=edtDireccion.text.toString()
                val ruc=edtRuc.text.toString()
                val telefono=edtTelefono.text.toString()


                val establecimineto=establecimientoDao.obtener()
                val rucRepetido=establecimineto.any { it.rucestablecimiento==ruc}
                val direccionRepetida=establecimineto.any { it.direccionestablecimiento==direccion}

                if(rucRepetido){
                    mostrarToast("El ruc se repite")
                    return@launch
                }
                if(direccionRepetida){
                    mostrarToast("La direccion se repite")
                    return@launch
                }


                val bean= Establecimiento(nomEstablecimiento = nombre,
                    direccionestablecimiento = direccion,
                    rucestablecimiento = ruc,
                    telefonoestablecimiento = telefono)
                val establecimientoid=establecimientoDao.guardar(bean)

                agregarEstablecimientoMySql(bean)

                //CRto
                val beanNoSql = EstablecimientoNoSql(bean.nomEstablecimiento,bean.telefonoestablecimiento,
                    bean.direccionestablecimiento,bean.rucestablecimiento)
                bd.child("establecimiento").child(establecimientoid.toString()).setValue(beanNoSql).addOnCompleteListener{
                    mostrarToast("Establecimiento agregado correctamente")
                }

                volver()

            }

        }

    }
    fun agregarEstablecimientoMySql(bean:Establecimiento) {
        apiEstablecimiento.fetchGuardarEstablecimiento(bean).enqueue(object:Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

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

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
    fun volver() {
        val intent = Intent(this, DatosEstablecimiento::class.java)
        startActivity(intent)
    }
    fun conectar(){

        //inicar mi firebase
        FirebaseApp.initializeApp(this)
        bd=FirebaseDatabase.getInstance().reference
    }





}