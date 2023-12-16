package com.example.project_kotlin.dao

import androidx.room.*
import com.example.project_kotlin.entidades.Cargo

@Dao
interface CargoDao {
    @Query("SELECT * FROM cargo")
     fun obtenerTodo() : List<Cargo>

    @Query("SELECT * FROM cargo WHERE id = :id")
     fun obtenerPorId(id: Long) : Cargo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun guardar(cargo: Cargo) : Long

    @Update
     fun actualizar(cargo: Cargo)

    @Delete
     fun eliminar(cargo: Cargo)
}