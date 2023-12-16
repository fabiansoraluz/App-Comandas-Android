package com.example.project_kotlin.vistas.inicio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.project_kotlin.R
import com.example.project_kotlin.vistas.MainActivity

class InicioAPP : AppCompatActivity(), View.OnClickListener  {

    lateinit var btnIniciarSesion : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio_prueba)

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)

        btnIniciarSesion.setOnClickListener({loginAPP()})

    }

    fun loginAPP(){
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}