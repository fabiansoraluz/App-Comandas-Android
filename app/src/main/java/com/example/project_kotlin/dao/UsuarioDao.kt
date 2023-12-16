package com.example.project_kotlin.dao

import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.project_kotlin.entidades.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario")
     fun obtenerTodo() : List<Usuario>

    @Query("SELECT last_insert_rowid() FROM usuario")
    fun obtenerUltimoId(): Long

    @Query("SELECT * FROM usuario WHERE id = :id")
     fun obtenerPorId(id: Long) : Usuario

    @Query("SELECT * FROM usuario WHERE correo = :correo")
    fun obtenerPorCorreo(correo: String) : Usuario

    @Query("SELECT * FROM usuario WHERE correo = :correo AND contraseña = :contraseña")
    fun verificarcorreoycontraseña(correo: String,contraseña:String):Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun guardar(usuario: Usuario) : Long

    @Update
     fun actualizar(usuario: Usuario)

    @Delete
     fun eliminar(usuario: Usuario)
}