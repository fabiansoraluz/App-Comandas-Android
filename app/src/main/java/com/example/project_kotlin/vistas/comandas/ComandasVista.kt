package com.example.project_kotlin.vistas.comandas

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_kotlin.R
import com.example.project_kotlin.adaptador.adaptadores.comandas.ComandaAdapter
import com.example.project_kotlin.adaptador.adaptadores.mesas.ConfiguracionMesasAdapter
import com.example.project_kotlin.dao.ComandaDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.empleados.DatosEmpleados
import com.example.project_kotlin.vistas.inicio.IndexComandasActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ComandasVista:AppCompatActivity() {


    private lateinit var rvComanda : RecyclerView
    private lateinit var btnNuevaComanda : Button
    private lateinit var btnVolverConfi : Button
    private lateinit var comandaDao : ComandaDao
    private lateinit var tvSinComandas : TextView
    private lateinit var adaptador : ComandaAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.man_comanda)
        rvComanda = findViewById(R.id.rvComanda)
        btnNuevaComanda = findViewById(R.id.btnNuevaComanda)
        btnVolverConfi = findViewById(R.id.btnVolverConfi)
        btnVolverConfi.setOnClickListener({volver()})
        btnNuevaComanda.setOnClickListener({nuevaComanda()})
        tvSinComandas = findViewById(R.id.tvSinComandas)
        comandaDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).comandaDao()
        obtenerComandas()
    }
    fun obtenerComandas(){
        lifecycleScope.launch(Dispatchers.IO){
            var datos = comandaDao.ComandasSinPagar()

            withContext(Dispatchers.Main) {
                if(datos.size != 0)
                    tvSinComandas.visibility = View.GONE
                adaptador = ComandaAdapter(datos)
                rvComanda.layoutManager= LinearLayoutManager(this@ComandasVista)
                rvComanda.adapter = adaptador
            }
        }
    }
    fun nuevaComanda(){
        var intent = Intent(this, RegistrarComanda::class.java)
        startActivity(intent)
    }

    fun volver(){
        var intent = Intent(this, IndexComandasActivity::class.java)
        startActivity(intent)
    }

}