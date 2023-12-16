package com.example.project_kotlin

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.project_kotlin.dao.*
import com.example.project_kotlin.db.ComandaDatabase
import com.example.project_kotlin.entidades.*
import com.example.project_kotlin.utils.appConfig
import org.junit.*
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AperturaDaoTest {
    private lateinit var estableci : EstablecimientoDao
    private lateinit var cajaDao: CajaDao
    private lateinit var cargoDao : CargoDao
    private lateinit var categoriaPlatoDao: CategoriaPlatoDao
    private lateinit var db: ComandaDatabase
    private lateinit var platoDao: PlatoDao

    @Before
    fun crearDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ComandaDatabase::class.java).build()
        categoriaPlatoDao = db.categoriaPlatoDao()
        cajaDao = db.cajaDao()
        platoDao = db.platoDao()
        cargoDao = ComandaDatabase.obtenerBaseDatos(appConfig.CONTEXT).cargoDao()

    }

    @After
    @Throws(IOException::class)
    fun cerrarDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun registrarCajaAperturaLeerCajaConApertura() {


        System.out.println(cargoDao.obtenerTodo())


    }
}