package com.example.project_kotlin.vistas.establecimiento

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.establecimiento.ConfiguracionEstablecimientoAdapter
import com.example.project_kotlin.dao.EstablecimientoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.Establecimiento
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.inicio.ConfiguracionVista
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DatosEstablecimiento: AppCompatActivity() {


        private lateinit var btnNuevoEstablecimiento:Button
        private lateinit var btnVoler:Button


        private lateinit var txtOcultarRegistros:TextView

        private lateinit var  establecimientoDao:EstablecimientoDao
        private lateinit var adaptador : ConfiguracionEstablecimientoAdapter
        private lateinit var rvEstablecimiento : RecyclerView





        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.configuracion_establecimiento)


                btnNuevoEstablecimiento = findViewById(R.id.btnNuevoEstablecimiento)
                btnVoler = findViewById(R.id.btnRegresarIndexEstablecimiento)
                rvEstablecimiento = findViewById(R.id.rvListadoMesasCon)
                txtOcultarRegistros=findViewById(R.id.txtOcultarRegistros)


                btnNuevoEstablecimiento.setOnClickListener({ adicionar() })
                btnVoler.setOnClickListener({ volverIndex() })
                establecimientoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).establecimientoDao()
                obtenerListadoEstablecimiento()


        }

        fun obtenerListadoEstablecimiento() {
                lifecycleScope.launch(Dispatchers.IO) {
                        val datos = establecimientoDao.obtenerTodoLiveData()

                        withContext(Dispatchers.Main) {
                                datos.observe(this@DatosEstablecimiento) { listaDatos ->
                                        if (!listaDatos.isNullOrEmpty()) {
                                                adaptador = ConfiguracionEstablecimientoAdapter(listaDatos)
                                                rvEstablecimiento.layoutManager = LinearLayoutManager(this@DatosEstablecimiento)
                                                rvEstablecimiento.adapter = adaptador
                                                txtOcultarRegistros.setText("")
                                        } else {

                                                txtOcultarRegistros.setText("No hay Registros")
                                        }
                                }
                        }
                }
        }


        fun volverIndex(){
                var intent = Intent(this, ConfiguracionVista::class.java)
                startActivity(intent)
        }

        fun adicionar(){
                var intent = Intent(this, NuevoEstablecimiento::class.java)
                startActivity(intent)
        }
        private fun mostrarToast(mensaje: String) {
                runOnUiThread {
                        Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
                }
        }


}