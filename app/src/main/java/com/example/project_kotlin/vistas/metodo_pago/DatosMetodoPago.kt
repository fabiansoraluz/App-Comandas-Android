package com.example.project_kotlin.vistas.metodo_pago

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.project_kotlin.R
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.adaptador.adaptadores.motodopago.MetodoPagoAdapter
import com.example.project_kotlin.dao.MetodoPagoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.MetodoPago
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.ConfiguracionVista
import com.example.project_kotlin.vistas.mesas.NuevaMesa
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatosMetodoPago:AppCompatActivity() {

    private lateinit var edtBuscarPago: TextView
    private lateinit var rvMetodoPago: RecyclerView
    private lateinit var btnAgregarMetPago: Button
    private lateinit var btnVolverMetPago: Button

    private lateinit var metodoPagoDao: MetodoPagoDao
    private lateinit var adaptador: MetodoPagoAdapter
    private var nomMetPago: String = " "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.metodo_pago)

        edtBuscarPago = findViewById(R.id.edtBuscarPago)
        rvMetodoPago = findViewById(R.id.rvMetodoPago)
        btnAgregarMetPago = findViewById(R.id.btnAgregarMetPago)
        btnVolverMetPago = findViewById(R.id.btnVolverMetPago)

        metodoPagoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).metodoPagoDao()

        btnAgregarMetPago.setOnClickListener({ adicionar() })
        btnVolverMetPago.setOnClickListener({ volverIndex() })
        obtenerMetodoPago()

        //filtrar nombre
        edtBuscarPago.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    val input = p0.toString()
                    val regex =
                        Regex("^[A-Za-z ]+\$") // Expresión regular que permite letras y espacios

                    if (!regex.matches(input)) {
                        edtBuscarPago.text = "" // Borra el texto si no cumple con la expresión regular
                        mostrarToast("No se permite el ingreso de números o caracteres especiales")
                        return
                    }

                    nomMetPago = p0.toString()

                }else{
                    nomMetPago = ""
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                filtrar(nomMetPago)
            }

        })
    }

    fun filtrar(nomMetPago: String) {
        lifecycleScope.launch(Dispatchers.IO){
            var datos = metodoPagoDao.buscarTodo()
            var datosFiltrados:List<MetodoPago> = datos
            if (!nomMetPago.isNullOrEmpty()) {

                datosFiltrados = datosFiltrados.filter { metodoPago ->
                    metodoPago.nombreMetodoPago.contains(nomMetPago, ignoreCase = true)
                }
            }

            withContext(Dispatchers.Main) {
                adaptador.ActualizarMetodoPago(datosFiltrados)
            }
        }
    }

    fun obtenerMetodoPago() {
            lifecycleScope.launch(Dispatchers.IO){
                var datos = metodoPagoDao.obtenerTodoLiveData()
                withContext(Dispatchers.Main){
                    datos.observe(this@DatosMetodoPago){ metodosPagos ->

                        adaptador = MetodoPagoAdapter(metodosPagos)
                        rvMetodoPago.layoutManager = LinearLayoutManager(this@DatosMetodoPago)
                        rvMetodoPago.adapter = adaptador

                    }
                }
            }
        }

        fun volverIndex() {
            var intent = Intent(this, ConfiguracionVista::class.java)
            startActivity(intent)
        }

        fun adicionar() {
            var intent = Intent(this, NuevoMetodoPago::class.java)
            startActivity(intent)
        }

        private fun mostrarToast(mensaje: String) {
            runOnUiThread {
                Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
            }
        }


    }