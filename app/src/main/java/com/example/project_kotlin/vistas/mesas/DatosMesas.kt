package com.example.project_kotlin.vistas.mesas

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.text.TextWatcher
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.mesas.ConfiguracionMesasAdapter
import com.example.project_kotlin.dao.MesaDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Mesa
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.ConfiguracionVista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatosMesas : AppCompatActivity(){
    private lateinit var spEstadoMesa : Spinner
    private lateinit var edBuscarNumAsientos : EditText
    private lateinit var rvMesas : RecyclerView
    private lateinit var btnNuevaMesa : Button
    private lateinit var btnVolverIndexMesa : Button
    private lateinit var tvEtiqueta : TextView
    private lateinit var mesaDao : MesaDao
    private lateinit var adaptador : ConfiguracionMesasAdapter
    private  var cantMesasFiltro : Int = 0
    private var estadoMesaFiltro : String = "Seleccionar estado"

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.con_mesas)
        btnNuevaMesa = findViewById(R.id.btnNuevoEmpleadoCon)
        btnVolverIndexMesa = findViewById(R.id.btnCancelarCategoria)
        rvMesas = findViewById(R.id.rvListadoMesasCon)
        spEstadoMesa = findViewById(R.id.spnFiltrarEstadoMesas)
        edBuscarNumAsientos = findViewById(R.id.edtBuscarMesas)
        btnNuevaMesa.setOnClickListener({adicionar()})
        tvEtiqueta = findViewById(R.id.tvDatosMesasSinDatos)
        btnVolverIndexMesa.setOnClickListener({volverIndex()})
        mesaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).mesaDao()
        obtenerMesas()

        //Estados a los campos para filtrado
        edBuscarNumAsientos.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!p0.isNullOrEmpty()) {
                    if (p0.toString().matches(Regex("\\d+"))) {
                        cantMesasFiltro = p0.toString().toInt()
                    } else {
                        edBuscarNumAsientos.setText("") // Borra el texto si no es un número
                        cantMesasFiltro = 0
                        mostrarToast("No se permite el ingreso de letras en este campo")
                    }
                } else {
                    cantMesasFiltro = 0
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                filtrar(estadoMesaFiltro, cantMesasFiltro)
            }

        })
        spEstadoMesa.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spEstadoMesa.getItemAtPosition(position).toString()
                estadoMesaFiltro = selectedItem
                filtrar(estadoMesaFiltro, cantMesasFiltro)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Opcionalmente, puedes manejar el caso de no haber nada seleccionado
            }
        })

    }
    fun volverIndex(){
        var intent = Intent(this, ConfiguracionVista::class.java)
        startActivity(intent)
    }
    fun filtrar(estadoMesa : String, cantAsientos : Int){
        lifecycleScope.launch(Dispatchers.IO){
            var datos = mesaDao.obtenerTodo()
            var datosFiltrados : List<Mesa> = datos
            if(!estadoMesa.equals("Seleccionar estado")){
                datosFiltrados = datosFiltrados.filter { mesa -> mesa.estado.equals(estadoMesa) }
            }
            if(cantAsientos != 0){
                datosFiltrados = datosFiltrados.filter { mesa -> mesa.cantidadAsientos == cantAsientos }
            }
            withContext(Dispatchers.Main){
                adaptador.actualizarListaMesas(datosFiltrados)

            }


        }
    }

     fun obtenerMesas() {
        lifecycleScope.launch(Dispatchers.IO){
            var datos = mesaDao.obtenerTodo()
            if(datos.size != 0)
                tvEtiqueta.visibility = View.GONE

            withContext(Dispatchers.Main) {
                adaptador = ConfiguracionMesasAdapter(datos)
                rvMesas.layoutManager=LinearLayoutManager(this@DatosMesas)
                rvMesas.adapter = adaptador
            }


        }
    }

    fun adicionar(){
        var intent = Intent(this, NuevaMesa::class.java)
        startActivity(intent)
    }
    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}