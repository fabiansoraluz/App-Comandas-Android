package com.example.project_kotlin

import android.app.Application
import com.example.project_kotlin.db.ComandaDatabase

class ComandaApplication : Application() {
    val database: ComandaDatabase by lazy { ComandaDatabase.obtenerBaseDatos(this) }
}
