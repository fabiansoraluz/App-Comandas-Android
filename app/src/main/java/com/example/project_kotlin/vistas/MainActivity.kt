package com.example.project_kotlin.vistas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.EmpleadoDao
import com.example.project_kotlin.dao.EstablecimientoDao
import com.example.project_kotlin.dao.UsuarioDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Empleado
import com.example.project_kotlin.utils.VariablesGlobales
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.IndexComandasActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var btnIngresar: Button
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var empleadoDao: EmpleadoDao
    private lateinit var edtEmail: EditText
    private lateinit var edtContraseña: EditText
    private lateinit var imgShow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edtEmail = findViewById(R.id.edtEmail)
        edtContraseña = findViewById(R.id.edtPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        imgShow=findViewById(R.id.imgShow)
        usuarioDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).usuarioDao()
        empleadoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).empleadoDao()
        btnIngresar.setOnClickListener({ vincular() })
        imgShow.setOnClickListener {
            if (edtContraseña.transformationMethod == PasswordTransformationMethod.getInstance()) {
                // Mostrar contraseña
                edtContraseña.transformationMethod = HideReturnsTransformationMethod.getInstance()

            } else {
                // Ocultar contraseña
                edtContraseña.transformationMethod = PasswordTransformationMethod.getInstance()

            }
        }

    }

    fun vincular() {

        val email = edtEmail.text.toString()
        val password = edtContraseña.text.toString()

        lifecycleScope.launch(Dispatchers.IO) {
            val verificarusuario = usuarioDao.verificarcorreoycontraseña(email, password)

            launch(Dispatchers.Main) {
                if (verificarusuario != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        var id = verificarusuario.id
                        if(id != null){
                            VariablesGlobales.empleado = empleadoDao.obtenerPorId(id)
                        }
                    }
                    mostrarToast("Ingresando al sistema")
                    val intent = Intent(this@MainActivity, IndexComandasActivity::class.java)
                    startActivity(intent)
                } else {
                    mostrarToast("Verifique su email o contraseña")
                }
            }
        }


    }
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}



