package com.example.project_kotlin.dao

import androidx.room.*
import com.example.project_kotlin.entidades.Mesa
import com.example.project_kotlin.entidades.relaciones.MesaConComandas

@Dao
interface MesaDao {
    @Query("SELECT * FROM mesa")
     fun obtenerTodo() : List<Mesa>

    @Query("SELECT * FROM mesa WHERE estado_mesa = 'Libre'")
     fun obtenerMesasLibres() : List<Mesa>

    @Transaction
    @Query("SELECT * FROM mesa WHERE id = :id")
     fun obtenerPorId(id: Long) : MesaConComandas

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun guardar(mesa: Mesa) : Long

    @Update
     fun actualizar(mesa: Mesa)

    @Delete
     fun eliminar(mesa: Mesa)

     @Transaction
     @Query("SELECT * FROM mesa")
     fun obtenerMesasConComandas(): List<MesaConComandas>
}