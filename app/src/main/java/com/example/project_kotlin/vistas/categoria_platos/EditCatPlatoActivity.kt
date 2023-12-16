package com.example.project_kotlin.vistas.categoria_platos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.CategoriaPlatoDao
import com.example.project_kotlin.dao.PlatoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.CategoriaPlato
import com.example.project_kotlin.entidades.dto.CategoriaPlatoDTO
import com.example.project_kotlin.entidades.firebase.CategoriaPlatoNoSql
import com.example.project_kotlin.service.ApiServiceCategoriaPlato
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

class EditCatPlatoActivity:AppCompatActivity() {
    private lateinit var tvCodCategoriaPlatos:TextView
    private lateinit var edtCategoriaNombres:EditText
    private lateinit var btnEditar:Button
    private lateinit var btnCancelar:Button
    private lateinit var btnEliminar:Button

    private lateinit var cateBean : CategoriaPlato

    private lateinit var cateDAO: CategoriaPlatoDao
    private lateinit var prodDAO: PlatoDao

    //REST
    lateinit var apiCategoria : ApiServiceCategoriaPlato
    //Firebase
    lateinit var bdFirebase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.categoriaplatoeditar)

        tvCodCategoriaPlatos = findViewById(R.id.tvCodCategoriaPlatos)
        edtCategoriaNombres = findViewById(R.id.edtCategoriaNombre)
        btnEditar = findViewById(R.id.btnEditarCategoria)
        btnCancelar= findViewById(R.id.btnCancelarCat)

        apiCategoria = ApiUtils.getAPIServiceCategoriaPlato();
        btnEliminar = findViewById(R.id.btnEliminarCat)
        cateDAO = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        prodDAO = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).platoDao()

        btnEditar.setOnClickListener({Editar()})
        btnCancelar.setOnClickListener({Cancelar()})
        btnEliminar.setOnClickListener({Eliminar()})

        cateBean = intent.getSerializableExtra("categoriaPlato") as CategoriaPlato
        tvCodCategoriaPlatos.setText(cateBean.id)
        edtCategoriaNombres.setText(cateBean.categoria)

        conectar()


    }
    fun Cancelar(){
        var intent= Intent(this, CategoriaPlatosActivity::class.java)
        startActivity(intent)
    }

    fun Editar() {
        val idCategoria = tvCodCategoriaPlatos.text.toString()
        lifecycleScope.launch(Dispatchers.IO) {

                if (validarCampos()) {
                    val nombreCategoria = edtCategoriaNombres.text.toString()
                    val catego = CategoriaPlato(idCategoria, nombreCategoria)
                    val cateDTO = CategoriaPlatoDTO(idCategoria, nombreCategoria)
                    cateDAO.actualizar(catego)
                    actualizarCateMySql(cateDTO)
                    //FIREBASE
                    val numero = idCategoria.substringAfter('-').toInt()
                    val idCatPlato = numero.toString()
                    val cateNoSqL = CategoriaPlatoNoSql(catego.categoria)
                    bdFirebase.child("establecimiento").child(idCatPlato).setValue(cateNoSqL)
                    mostrarToast("Categoria actualizada correctamente")
                    Volver()
                }

        }
    }

    fun Eliminar() {
        val codCate = tvCodCategoriaPlatos.text.toString()
        val mensaje: AlertDialog.Builder = AlertDialog.Builder(this)
        mensaje.setTitle("Sistema comandas")
        mensaje.setMessage("¿Seguro de eliminar?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                //Validar de comandas
                val validarCategoria = prodDAO.obtenerPlatosPorCategoria(codCate)
                if(validarCategoria.isEmpty()){
                    val eliminar = cateDAO.obtenerPorId(codCate)
                    cateDAO.eliminar(eliminar)
                    eliminarMysql(codCate)
                    //FIREBASE
                    val numero = codCate.substringAfter('-').toInt()
                    val idCatPlato = numero.toString()
                    bdFirebase.child("categoria").child(idCatPlato).removeValue()
                    mostrarToast("Categoria eliminada correctamente")
                    Volver()
                }else{
                    mostrarToast("La categoría que tiene productos registrados no puede ser eliminado")
                }


            }
        }
        mensaje.setNegativeButton("Cancelar") { _, _ -> }
        mensaje.setIcon(android.R.drawable.ic_delete)
        mensaje.show()
    }

    fun eliminarMysql(id:String){
        apiCategoria.fetcEliminarCategoria(id).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }

    fun actualizarCateMySql(bean: CategoriaPlatoDTO){
        apiCategoria.fetchActualizarCategoria(bean).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }

    fun Volver() {
        val intent = Intent(this, CategoriaPlatosActivity::class.java)
        startActivity(intent)
    }

    fun validarCampos(): Boolean {
        val cateNombre = edtCategoriaNombres.text.toString()
        val regex = Regex("[0-9]")

        if (cateNombre.isEmpty() || regex.containsMatchIn(cateNombre)) {
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

    fun conectar(){
        //Iniciar firebase en la clase actual
        FirebaseApp.initializeApp(this)
        bdFirebase = FirebaseDatabase.getInstance().reference
    }
}