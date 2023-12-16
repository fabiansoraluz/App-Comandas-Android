package com.example.project_kotlin.vistas.platos

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.CategoriaPlatoDao
import com.example.project_kotlin.dao.PlatoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.CategoriaPlato
import com.example.project_kotlin.entidades.Plato
import com.example.project_kotlin.entidades.PlatoConCategoria
import com.example.project_kotlin.entidades.dto.PlatoDTO
import com.example.project_kotlin.entidades.firebase.CategoriaPlatoNoSql
import com.example.project_kotlin.entidades.firebase.PlatoNoSql
import com.example.project_kotlin.service.ApiServicePlato
import com.example.project_kotlin.utils.ApiUtils
import com.example.project_kotlin.utils.appConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


@Suppress("DEPRECATION")
class NuevoPlato : AppCompatActivity() {

    private lateinit var edtNombrePlato: EditText
    private lateinit var edtPrecioPlato: EditText
    private lateinit var btnImagen: Button
    private lateinit var spcategoria: Spinner
    private lateinit var btnAgregarplato: Button
    private lateinit var btnCancelar: Button

    //BASE DE DATOS
    private lateinit var platoDao: PlatoDao
    private lateinit var categoriaPlatoDao: CategoriaPlatoDao
    //REST
    lateinit var apiPlato: ApiServicePlato

    //FIREBASE
    lateinit var bdFirebase: DatabaseReference


    //ADICIONALES
    private var estadoCatFiltro: String = "Seleccionar Categoria"
    private val PICK_IMAGE_REQUEST = 1


    private var imageData: ByteArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.platoregistrar)

        edtNombrePlato = findViewById(R.id.edtBuscarNombreUsu)
        edtPrecioPlato = findViewById(R.id.edtPrePlato)
        btnImagen = findViewById(R.id.btnAplicarUsu)
        spcategoria = findViewById(R.id.spnCargoEmpleadoE)
        btnAgregarplato = findViewById(R.id.btnNuevoUsu)
        btnCancelar = findViewById(R.id.btnCancelarCategoria)
        //BASES DE DATOS
        platoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).platoDao()
        categoriaPlatoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        apiPlato = ApiUtils.getAPIServicePlato()




        btnAgregarplato.setOnClickListener({ Agregar() })

        btnImagen.setOnClickListener {
            // Lanzar la selección de imágenes desde la galería
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }


        btnCancelar.setOnClickListener({ Cancelar() })
        conectar()
        cargarCategoria()
    }

    fun conectar() {
        //Iniciar firebase en la clase actual
        FirebaseApp.initializeApp(this)
        bdFirebase = FirebaseDatabase.getInstance().reference
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Obtener la imagen seleccionada
            val imageUri = data.data
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

            // Convertir la imagen en un ByteArray
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            imageData = outputStream.toByteArray()

            // Mostrar la imagen seleccionada en una ImageView (opcional)
            val imageView = findViewById<ImageView>(R.id.imagenCrear)
            imageView.setImageBitmap(bitmap)
        }
    }

    fun Agregar() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (validarCampos()) {
                if (imageData != null) {
                    val nombrep = edtNombrePlato.text.toString().trim()
                    val listaPlatos = platoDao.obtenerTodo()
                    for (plato in listaPlatos) {
                        val validaplato= plato.plato.nombrePlato.trim().lowercase()
                        if (nombrep.lowercase() == validaplato) {
                            mostrarToast("No se puede registrar el mismo plato")
                            return@launch
                        }
                    }

                    val codigo = Plato.generarCodigo(platoDao.obtenerTodo())
                    val precio = edtPrecioPlato.text.toString().toDouble()
                    val codCatPlato = (spcategoria.selectedItemPosition).toString()
                    val cat = "C-00$codCatPlato"
                    val categoriaPlato = CategoriaPlato(cat, spcategoria.selectedItem.toString())
                    val base64String = android.util.Base64.encodeToString(imageData, android.util.Base64.DEFAULT)
                    val imageUrl = "data:image/jpeg;base64,$base64String"

                    // Crear objeto plato
                    val platoDTO = PlatoDTO(codigo, nombrep, imageUrl, precio, categoriaPlato)
                    grabarPlatoMysql(platoDTO)

                    // Guardar en Room
                    val plato = Plato(codigo, nombrep, precio, imageUrl, cat)
                    platoDao.guardar(plato)

                    // Guardar en Firebase
                    val numero = codigo.substringAfter('-').toInt()
                    val idCatPlato = numero.toString()
                    val categoriaPlatoNoSql = CategoriaPlatoNoSql(categoriaPlato.categoria)
                    val platoNoSql = PlatoNoSql(nombrep, imageUrl, precio, categoriaPlatoNoSql)
                    bdFirebase.child("plato").child(idCatPlato).setValue(platoNoSql)

                    mostrarToast("Plato agregado correctamente")
                    Cancelar()
                } else {
                    mostrarToast("Agregar imagen del plato")
                }
            }
        }
    }

    private fun Cancelar() {
        val intent = Intent(this, DatosPlatos::class.java)
        startActivity(intent)
    }

    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    fun grabarPlatoMysql(bean: PlatoDTO){
        apiPlato.fetchGuardarPlato(bean).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }
    private fun cargarCategoria() {
        lifecycleScope.launch(Dispatchers.IO) {

            // Obtén la lista de categorías de platos desde la base de datos
            var data = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao().obtenerTodo()

            var nombreCate = data.map { it.categoria }

            //Opcion por defecto
            val opciones = mutableListOf<String>()
            opciones.add("Seleccionar Categoria")
            opciones.addAll(nombreCate)

            // Crea un ArrayAdapter con los nombres de las categorías de platos
            var adapter = ArrayAdapter(
                this@NuevoPlato,
                android.R.layout.simple_spinner_item, opciones

            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Asigna el adaptador al Spinner
            spcategoria.adapter = adapter
        }
    }

    fun validarCampos(): Boolean {
        val nombre = edtNombrePlato.text.toString()
        val precio = edtPrecioPlato.text.toString()
        val spcat = spcategoria.selectedItem
        val REGEX_NOMBRE = "^[A-Z][a-zA-Z\\s]*\$"
        val REGEX_PRECIO = "^[\\d]{1,3}(?:,[\\d]{3})*(?:\\.[\\d]{1,2})?\$"
        if (!REGEX_NOMBRE.toRegex().matches(nombre)) {
            // El campo nombre no cumple con el formato esperado
            mostrarToast("Ingrese un nombre iniciado por Mayuscula la primera letra")
            return false
        }
        if (!REGEX_PRECIO.toRegex().matches(precio)) {
            // El campo nombre no cumple con el formato esperado
            mostrarToast("Ingrese un precio no maximo de 999.99")
            return false
        }
        if (spcat == estadoCatFiltro) {
            mostrarToast("Seleccione una categoría")
            return false
        }

     return true
    }
}



