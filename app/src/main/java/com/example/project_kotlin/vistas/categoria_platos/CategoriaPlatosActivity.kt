package com.example.project_kotlin.vistas.categoria_platos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.categoria_plato.CategoriaPlatoAdapter
import com.example.project_kotlin.dao.CategoriaPlatoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.ConfiguracionVista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriaPlatosActivity: AppCompatActivity() {
    private lateinit var rvCategoria: RecyclerView
    private lateinit var btnAgregarCategoria: Button
    private lateinit var btnRegresar: Button
    private lateinit var cateDao: CategoriaPlatoDao
    private lateinit var adaptador : CategoriaPlatoAdapter
    private lateinit var tvDAtosSinCategoria : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_categoriaplatos)


        cateDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        rvCategoria = findViewById(R.id.rvListadoMesasCon)
        btnAgregarCategoria = findViewById(R.id.btnAgregarCategoria)
        btnRegresar = findViewById(R.id.btnCancelarCategoria)
        tvDAtosSinCategoria = findViewById(R.id.tvCategoriaSinDatos)
        rvCategoria = findViewById(R.id.rvListadoMesasCon)

        btnAgregarCategoria.setOnClickListener({agregarVista()})
        btnRegresar.setOnClickListener({regresar()})
        obtenerCategorias()
    }

    fun agregarVista(){
        var intent= Intent(this, NewCatPlatoActivity::class.java)
        startActivity(intent)
    }
    fun regresar(){
        var intent= Intent(this, ConfiguracionVista::class.java)
        startActivity(intent)
    }

    fun obtenerCategorias() {
        lifecycleScope.launch(Dispatchers.IO){
            try {
                val datos = cateDao.obtenerTodoLiveData()
                withContext(Dispatchers.Main){
                    datos.observe(this@CategoriaPlatosActivity){categoria ->
                        if(categoria.isNotEmpty()){
                            adaptador = CategoriaPlatoAdapter(categoria)
                            rvCategoria.layoutManager = LinearLayoutManager(this@CategoriaPlatosActivity)
                            rvCategoria.adapter = adaptador

                            tvDAtosSinCategoria.visibility = View.GONE
                        }else{
                            tvDAtosSinCategoria.visibility = View.VISIBLE
                        }

                    }
                }
            }catch (e: Exception){
                val mensajeError = "Error al obtener los datos: ${e.message}"
                mostrarToast(mensajeError)
            }

        }
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }







}