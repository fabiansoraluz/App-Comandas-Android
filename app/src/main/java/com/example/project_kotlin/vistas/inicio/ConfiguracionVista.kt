package com.example.project_kotlin.vistas.inicio

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.project_kotlin.R
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.caja_registradora.DatosCajas
import com.example.project_kotlin.vistas.platos.DatosPlatos
import com.example.project_kotlin.vistas.categoria_platos.CategoriaPlatosActivity
import com.example.project_kotlin.vistas.empleados.DatosEmpleados

import com.example.project_kotlin.vistas.establecimiento.DatosEstablecimiento

import com.example.project_kotlin.vistas.mesas.DatosMesas
import com.example.project_kotlin.vistas.metodo_pago.DatosMetodoPago

class ConfiguracionVista:AppCompatActivity() {

    private lateinit var cvEmpleados:CardView
    private lateinit var cvPlatos:CardView
    private lateinit var cvMesas:CardView
    private lateinit var cvCategoriaPlatos:CardView
    private lateinit var cvEstablecimiento:CardView
    private lateinit var cvRegresarINdex:CardView
    private lateinit var cvMetodoPago:CardView
    private lateinit var cvCajas:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuracion)

        cvEmpleados = findViewById(R.id.cvEmpleados)
        cvPlatos = findViewById(R.id.cvPlatos)
        cvMesas = findViewById(R.id.cvMesas)
        cvCategoriaPlatos = findViewById(R.id.cvCategoriaPlatos)
        cvEstablecimiento = findViewById(R.id.cvEstablecimiento)
        cvMetodoPago = findViewById(R.id.cvMetodoPago)
        cvCajas = findViewById(R.id.cvCajas)
        cvRegresarINdex = findViewById(R.id.cvRegresarIndex)

        cvEmpleados.setOnClickListener({vincularEmpleados()})
        cvPlatos.setOnClickListener({vincularPlatos()})
        cvMesas.setOnClickListener({vincularMesas()})
        cvCategoriaPlatos.setOnClickListener({vincularCategoriaPlatos()})
        cvEstablecimiento.setOnClickListener({vincularEstablecimiento()})
        cvCajas.setOnClickListener({vincularCajaVista()})
        cvMetodoPago.setOnClickListener({vincularMetodoPago()})
        cvRegresarINdex.setOnClickListener({vincularREgresoIndex()})


    }

     fun vincularEmpleados() {

         var intent = Intent(this, DatosEmpleados::class.java)
         startActivity(intent)

    }

    fun vincularPlatos() {

        var intent = Intent(this, DatosPlatos::class.java)
        startActivity(intent)

    }

    //VISTA MESA --PARTE DE FABIAN
    fun vincularMesas() {

        var intent = Intent(appConfig.CONTEXT, DatosMesas::class.java)
        startActivity(intent)

    }

    //falta
    fun vincularCategoriaPlatos() {

        var intent = Intent(this, CategoriaPlatosActivity::class.java)
        startActivity(intent)

    }

    //falta
    fun vincularEstablecimiento() {

        var intent = Intent(this, DatosEstablecimiento::class.java)
        startActivity(intent)

    }

    //falta
    fun vincularMetodoPago(){

        var intent = Intent(this, DatosMetodoPago::class.java)
        startActivity(intent)

    }

    fun vincularCajaVista(){

        var intent = Intent(this, DatosCajas::class.java)
        startActivity(intent)

    }

    fun vincularREgresoIndex() {

        var intent = Intent(this, IndexComandasActivity::class.java)
        startActivity(intent)

    }



}