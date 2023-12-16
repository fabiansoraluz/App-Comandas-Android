package com.example.project_kotlin.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project_kotlin.entidades.EstadoComanda
import com.example.project_kotlin.entidades.Mesa

@Dao
interface EstadoComandaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(estadoComanda: EstadoComanda) : Long
    @Query("select * from EstadosComanda")
    fun obtenerTodo() : List<EstadoComanda>
}