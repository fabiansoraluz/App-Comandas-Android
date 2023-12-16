package com.example.project_kotlin.vistas.categoria_platos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.CategoriaPlatoDao
import com.example.project_kotlin.dao.MesaDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.CategoriaPlato
import com.example.project_kotlin.entidades.dto.CategoriaPlatoDTO
import com.example.project_kotlin.entidades.dto.EmpleadoDTO
import com.example.project_kotlin.entidades.firebase.CategoriaPlatoNoSql
import com.example.project_kotlin.service.ApiServiceCategoriaPlato
import com.example.project_kotlin.service.ApiServiceEmpleado
import com.example.project_kotlin.utils.ApiUtils
import com.example.project_kotlin.utils.appConfig
import com.example.project_kotlin.vistas.mesas.DatosMesas
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewCatPlatoActivity: AppCompatActivity() {

    private lateinit var edtCategoriaNombre:EditText
    private lateinit var btnAgregar:Button
    private lateinit var btnCancelar:Button

    //BAse de dATOS
    private lateinit var cateDao: CategoriaPlatoDao

    //REST
    lateinit var apiCategoria : ApiServiceCategoriaPlato
    //Firebase
    lateinit var bdFirebase : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categoriaplatoregistrar)

        cateDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        edtCategoriaNombre= findViewById(R.id.edtCategoriaNombre)
        btnAgregar = findViewById(R.id.btnAgregarCategoria)
        apiCategoria = ApiUtils.getAPIServiceCategoriaPlato();
        btnCancelar = findViewById(R.id.btnCancelarCategoria)
        btnAgregar.setOnClickListener({AgregarCategoria()})
        btnCancelar.setOnClickListener({volverIndex()})
        conectar()
    }

    fun Cancelar(){
        var intent= Intent(this, CategoriaPlatosActivity::class.java)
        startActivity(intent)
    }

    fun conectar(){
        //Iniciar firebase en la clase actual
        FirebaseApp.initializeApp(this)
        bdFirebase = FirebaseDatabase.getInstance().reference
    }


    fun AgregarCategoria() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (validarCampos()) {
                val nombre = edtCategoriaNombre.text.toString()

                // Verificar si el nombre de la categoría ya existe en la base de datos
                if (!categoriaExistente(nombre)) {
                    val codigo = CategoriaPlato.generarCodigo(cateDao.obtenerTodo())

                    //crear objeto categoria DTO
                    val cateDTO = CategoriaPlatoDTO(codigo, nombre)
                    grabarCateMySql(cateDTO)

                    //Guardar room
                    val categoriaPlato = CategoriaPlato(id = codigo, categoria = nombre)
                    cateDao.guardar(categoriaPlato)

                    //guardar en firebase
                    val numero = codigo.substringAfter('-').toInt()
                    val idCatPlato = numero.toString()
                    val cateNoSqL = CategoriaPlatoNoSql(categoriaPlato.categoria)
                    bdFirebase.child("categoria").child(idCatPlato).setValue(cateNoSqL)

                    mostrarToast("Categoría agregada correctamente")
                    volverIndex()
                } else {
                    mostrarToast("El nombre de la categoría ya existe")
                }
            }
        }
    }

    // Función para verificar si el nombre de la categoría ya existe en la base de datos
    suspend fun categoriaExistente(nombre: String): Boolean {
        val categorias = cateDao.obtenerTodo()
        return categorias.any { it.categoria == nombre }
    }


    fun grabarCateMySql(bean: CategoriaPlatoDTO){
        apiCategoria.fetchGuardarCategoria(bean).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }

    fun validarCampos(): Boolean {
        val cateNombre = findViewById<EditText>(R.id.edtCategoriaNombre)
        val nombreCategoria = cateNombre.text.toString()
        val regex = Regex("[0-9]")

        if (cateNombre.text.toString().isEmpty() || regex.containsMatchIn(nombreCategoria)) {
            mostrarToast("Ingrese categoría válida")
            return false
        }
        return true
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    fun volverIndex() {
        val intent = Intent(this, CategoriaPlatosActivity::class.java)
        startActivity(intent)
    }

}
