package com.example.project_kotlin.entidades

import androidx.room.*
import com.example.project_kotlin.utils.Utilidades
import java.util.*

@Entity(tableName = "usuario")
data class Usuario (
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    @ColumnInfo(name="correo") var correo: String? = null,
    @ColumnInfo(name="contraseña") var contrasena: String? = null):java.io.Serializable {


    //Generar Contraseña
    fun generarContrasenia(apellido: String): String {
        val utils : Utilidades = Utilidades()
        val nroCaracterExtraer = 2
        val nroRamdom: Int = utils.generarNumeroRandom(1, apellido.length - nroCaracterExtraer)
        val caracterApe = apellido.substring(nroRamdom, nroRamdom + nroCaracterExtraer)
        val mayusculaCaracterApe =
            caracterApe.substring(0, 1).uppercase(Locale.getDefault()) + caracterApe.substring(1)
        return mayusculaCaracterApe + "$" + utils.generarNumeroRandom(1000, 5000)
    }
}


