package com.example.project_kotlin.vistas.platos

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.project_kotlin.R
import com.example.project_kotlin.dao.CategoriaPlatoDao
import com.example.project_kotlin.dao.PlatoDao
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.*
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ActualizarPlato:AppCompatActivity() {
    private lateinit var tvCodPlatos:TextView
    private lateinit var edtNamePlato:EditText
    private lateinit var edtPrePlato:EditText
    private lateinit var ImagenPlatos:ImageView
    private  lateinit var spCatplato:Spinner
    private lateinit var btnEditar:Button
    private lateinit var btnCancelar:Button
    private lateinit var btnEliminar:Button
    private lateinit var btnImagenPlato:Button
    //BASE DE DATOS
    private lateinit var platoDao: PlatoDao
    private lateinit var categoriaPlatoDao: CategoriaPlatoDao
    private lateinit var platobean : PlatoConCategoria
    //REST
    lateinit var apiPlato: ApiServicePlato

    //FIREBASE
    lateinit var bdFirebase: DatabaseReference

    private val PICK_IMAGE_REQUEST = 1
    private var imageData1: ByteArray? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.platoeditar)
        platoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).platoDao()

        tvCodPlatos=findViewById(R.id.tvCodPlatos)
        edtNamePlato= findViewById(R.id.edtBuscarNombreUsu)
        edtPrePlato = findViewById(R.id.edtPrePlato)
        ImagenPlatos = findViewById(R.id.imagenCrear)
        spCatplato= findViewById(R.id.spnPlatoEditar)

        btnEditar= findViewById(R.id.btnNuevoUsu)
        btnCancelar= findViewById(R.id.btnCancelarCategoria)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnImagenPlato = findViewById(R.id.btnBuscarComprobantes)

        //BASES DE DATOS
        platoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).platoDao()
        categoriaPlatoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao()
        apiPlato = ApiUtils.getAPIServicePlato()
        platobean = intent.getSerializableExtra("plato") as PlatoConCategoria
        cargarCategoria()
        conectar()
        btnEditar.setOnClickListener{actualizar()}
        btnEliminar.setOnClickListener{Eliminar()}
        btnCancelar.setOnClickListener{volver()}


        btnImagenPlato.setOnClickListener {
            // Lanzar la selección de imágenes desde la galería
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        //Cargar dato
        tvCodPlatos.text = platobean.plato.id
        edtNamePlato.setText(platobean.plato.nombrePlato)
        edtPrePlato.setText(platobean.plato.precioPlato.toString())

        Glide.with(this@ActualizarPlato)
            .load(platobean.plato.nombreImagen)
            .placeholder(R.drawable.platos)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ImagenPlatos)

        //para ver la secuencia del codigo
        val numberPattern = Regex("\\d+")
        val numberMatch = numberPattern.find(platobean.plato.catplato_id)
        val number = numberMatch?.value?.toIntOrNull()
        if (number != null) {
            spCatplato.setSelection(number - 1)
        }


    }

    fun volver(){
        var intent= Intent(this, DatosPlatos::class.java)
        startActivity(intent)
    }

    @SuppressLint("SuspiciousIndentation")
    fun Eliminar() {
        val mensaje: AlertDialog.Builder = AlertDialog.Builder(this)
        mensaje.setTitle("Sistema comandas")
        mensaje.setMessage("¿Seguro de eliminar?")
        mensaje.setCancelable(false)
        mensaje.setPositiveButton("Aceptar") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                val platoComanda = platoDao.getPlatoConComandasById(platobean.plato.id)
                if(platoComanda.comandas.size != 0){
                    mostrarToast("No se puede eliminar comandados")
                    return@launch
                }
               platoDao.eliminar(platobean.plato)
                eliminarPlatoMysql(platobean.plato.id)

                val numero = platobean.plato.id.substringAfter('-').toInt()
                val idCatPlato = numero.toString()
                bdFirebase.child("plato").child(idCatPlato).removeValue()
                mostrarToast("Plato eliminado correctamente")
                volver()

            }
        }
        mensaje.setNegativeButton("Cancelar") { _, _ -> }
        mensaje.setIcon(android.R.drawable.ic_delete)
        mensaje.show()
    }


    fun actualizar() {
        if (validarCampos()) {
            if (imageData1 != null) {
                lifecycleScope.launch(Dispatchers.IO) {

                    val nombre = edtNamePlato.text.toString().trim()
                    val precio = edtPrePlato.text.toString().toDouble()
                    val codCatPlato = spCatplato.selectedItemPosition +1
                    val cat = "C-00$codCatPlato"
                    val base64String =
                        android.util.Base64.encodeToString(imageData1!!, android.util.Base64.DEFAULT)
                    val imageUrl = "data:image/jpeg;base64,$base64String"

                    //validando
                    val platsvali = platoDao.obtenerTodo()
                    val nombreplatoRepetido = platsvali.any{it.plato.nombrePlato ==nombre && it.plato.id != platobean.plato.id }
                            if(nombreplatoRepetido){
                                mostrarToast("El Plato ya existe en otro plato")
                                return@launch
                            }
                    platobean.plato.nombrePlato = nombre
                    platobean.plato.nombreImagen = imageUrl
                    platobean.plato.precioPlato = precio
                    platobean.plato.catplato_id = cat
                    platobean.categoriaPlato.categoria = cat
                    platobean.categoriaPlato.categoria = spCatplato.selectedItem.toString()
                    platoDao.actualizar(platobean.plato)

                    val platoDTO = PlatoDTO(platobean.plato.id, nombre, imageUrl, precio, platobean.categoriaPlato)
                    Log.e("Error al actualizar: ","" +platoDTO)
                    actualizarPlatoMysql(platoDTO)
                    val numero = platobean.plato.id.substringAfter('-').toInt()
                    val idCatPlato = numero.toString()
                    val platoNoSql = PlatoNoSql(nombre,imageUrl, precio, CategoriaPlatoNoSql(platobean.categoriaPlato.categoria))

                    bdFirebase.child("plato").child(idCatPlato).setValue(platoNoSql)
                    mostrarToast("Plato actualizada correctamente")
                    volver()
                }
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val nombre = edtNamePlato.text.toString().trim()

                    val precio = edtPrePlato.text.toString().toDouble()
                    val codCatPlato = spCatplato.selectedItemPosition + 1
                    val cat = "C-00$codCatPlato"
                    //validando
                    val platsvali = platoDao.obtenerTodo()
                    val nombreplatoRepetido = platsvali.any{it.plato.nombrePlato ==nombre && it.plato.id != platobean.plato.id }
                    if(nombreplatoRepetido){
                        mostrarToast("El Plato ya existe en otro plato")
                        return@launch
                    }
                    platobean.plato.nombrePlato = nombre
                    platobean.plato.precioPlato = precio
                    platobean.plato.catplato_id = cat
                    platobean.categoriaPlato.categoria = cat
                    platobean.categoriaPlato.categoria = spCatplato.selectedItem.toString()
                    platoDao.actualizar(platobean.plato)

                    val platoDTO = PlatoDTO(platobean.plato.id, nombre, platobean.plato.nombreImagen, precio, platobean.categoriaPlato)
                    Log.e("Error al actualizar: ","" +platoDTO)
                    actualizarPlatoMysql(platoDTO)
                    val numero = platobean.plato.id.substringAfter('-').toInt()
                    val idCatPlato = numero.toString()
                    val platoNoSql = PlatoNoSql(nombre," ", precio, CategoriaPlatoNoSql(platobean.categoriaPlato.categoria))

                    bdFirebase.child("plato").child(idCatPlato).setValue(platoNoSql)
                    mostrarToast("Plato actualizada correctamente")
                    volver()
                }
            }

        }
    }
    private fun cargarCategoria() {
        lifecycleScope.launch(Dispatchers.IO) {

            // Obtén la lista de categorías de platos desde la base de datos
            var data = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).categoriaPlatoDao().obtenerTodo()

            var nombreCat =   data.map {  it.categoria }
            // Crea un ArrayAdapter con los nombres de las categorías de platos
            var adapter = ArrayAdapter(
                this@ActualizarPlato,
                android.R.layout.simple_spinner_item,nombreCat

            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Asigna el adaptador al Spinner
            spCatplato.adapter = adapter
        }
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
            imageData1 = outputStream.toByteArray()

            // Mostrar la imagen seleccionada en una ImageView (opcional)
            val imageView = findViewById<ImageView>(R.id.imagenCrear)
            imageView.setImageBitmap(bitmap)
        }
    }

    fun validarCampos() : Boolean{
        val nombre = edtNamePlato.text.toString()
        val precio = edtPrePlato.text.toString()
        val REGEX_NOMBRE = "^[A-Z][a-zA-Z\\s]*\$"
        val REGEX_PRECIO = "^[\\d]{1,3}(?:,[\\d]{3})*(?:\\.[\\d]{1,2})?\$"

        if (!REGEX_NOMBRE.toRegex().matches(nombre)) {
            // El campo nombre no cumple con el formato esperado
            mostrarToast("El campo nombre no cumple con el formato requerido. Debe comenzar con mayúscula y contener solo letras y espacios")
            return false
        }
        if (!REGEX_PRECIO.toRegex().matches(precio)) {
            // El campo nombre no cumple con el formato esperado
            mostrarToast("Ingrese un precio correcto")
            return false
        }
        return true
    }
    private fun mostrarToast(mensaje: String) {
        runOnUiThread {
            Toast.makeText(appConfig.CONTEXT, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    fun actualizarPlatoMysql(bean: PlatoDTO){
        apiPlato.fectchActualizarPlato(bean).enqueue(object:Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error al actualizar: ",t.toString())
            }
        })
    }

    fun eliminarPlatoMysql(id:String){
        apiPlato.fetchEliminarPlato(id).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Error : ",t.toString())
            }
        })
    }
    fun conectar(){
        //inicar mi firebase
        FirebaseApp.initializeApp(this)
        bdFirebase= FirebaseDatabase.getInstance().reference
    }
}

