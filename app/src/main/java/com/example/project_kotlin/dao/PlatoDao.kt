package com.example.project_kotlin.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project_kotlin.entidades.*

@Dao
interface PlatoDao {
    @Query("select * from Plato")
    fun obtenerTodoLiveData():LiveData<List<PlatoConCategoria>>

    @Query("select * from Plato ")
    fun obtenerTodo():List<PlatoConCategoria>


    @Query("select * from Plato where catplato_id = :catplato_id")
    fun obtenerPlatosPorCategoria(catplato_id: String): List<PlatoConCategoria>

    @Query("SELECT * FROM Plato WHERE id = :id")
    fun obtenerPorId(id: String) : Plato

    @Transaction
    @Query("SELECT * FROM Plato WHERE id = :platoId")
    fun getPlatoConComandasById(platoId: String): PlatosConComandas
    @Query("SELECT * FROM Plato WHERE nom_plato = :id")
    fun obtenerPorNombre(id: String) : Plato

    @Insert
    fun guardar(plato: Plato)

    @Update
    fun actualizar(plato: Plato)

    @Delete
    fun eliminar(plato:Plato)
}