package com.example.project_kotlin.dao

import androidx.room.*
import com.example.project_kotlin.entidades.*

@Dao
interface CajaDao {
    @Query("SELECT * FROM caja")
     fun obtenerTodo() : List<Caja>

    @Query("SELECT * FROM caja WHERE id = :id")
     fun obtenerPorId(id: String) : Caja
    @Query("SELECT * FROM Caja WHERE establecimiento_id = :establecimiento_id")
    fun obtenerCajaPorEstablecimiento(establecimiento_id: Int) : List<Caja>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun guardar(caja: Caja) : Long

    @Update
     fun actualizar(caja: Caja)


    @Delete
     fun eliminar(caja: Caja)
    @Transaction
    @Query("SELECT * FROM caja")
    fun obtenerCajaConComprobantes(): List<CajaConComprobantes>
}