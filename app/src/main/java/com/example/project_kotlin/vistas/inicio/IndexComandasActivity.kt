package com.example.project_kotlin.vistas.inicio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.project_kotlin.R
import com.example.project_kotlin.utils.VariablesGlobales
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.MainActivity
import com.example.project_kotlin.vistas.facturar.DatosComprobantes
import com.example.project_kotlin.vistas.comandas.ComandasVista

class IndexComandasActivity : AppCompatActivity() {

    lateinit var cvConfig : CardView
    lateinit var cvCajaRegis : CardView
    lateinit var cvPedidos : CardView
    lateinit var btnCerrarSession : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.index_comandas)

        cvConfig = findViewById(R.id.cvConfiguracion)
        cvCajaRegis = findViewById(R.id.cvCajaRegistradora)
        cvPedidos = findViewById(R.id.cvPedidos)
        btnCerrarSession = findViewById(R.id.btnCerrarSesion)

        btnCerrarSession.setOnClickListener({cerrarSesion()})
        cvConfig.setOnClickListener({vincularConfig()})
        cvCajaRegis.setOnClickListener({vincularCaja()})
        cvPedidos.setOnClickListener({vincularComandas()})

    }
    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        cerrarSesion()
    }
    fun cerrarSesion(){
        var intent = Intent(this, MainActivity::class.java)
        VariablesGlobales.empleado = null
        finish()
        startActivity(intent)
    }

    fun vincularConfig(){
        if(VariablesGlobales.empleado?.empleado?.cargo?.id == 1.toLong()){
            var intent = Intent(this, ConfiguracionVista::class.java)
            startActivity(intent)
        }else{
            mostrarToast("No tienes los permisos de administrador")
        }
    }
    fun vincularCaja(){
        var intent = Intent(this, DatosComprobantes::class.java)
        startActivity(intent)
    }

    //PEDIDOS FALTA
    fun vincularComandas(){
        if(VariablesGlobales.empleado?.empleado?.cargo?.id != 3.toLong()){
            var intent = Intent(this, ComandasVista::class.java)
            startActivity(intent)
        }else{
            mostrarToast("No tienes los permisos para acceder")
        }
    }




}