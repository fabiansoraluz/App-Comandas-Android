package com.example.project_kotlin.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project_kotlin.entidades.CategoriaConPlatos
import com.example.project_kotlin.entidades.CategoriaPlato
import com.example.project_kotlin.entidades.Empleado
import com.example.project_kotlin.entidades.relaciones.MesaConComandas

@Dao
interface CategoriaPlatoDao {
    @Transaction
    @Query("SELECT * FROM categoria_plato WHERE id = :id")
    fun obtenerCategoriaConPlatos(id: Long) : CategoriaConPlatos

    @Query("select * from categoria_plato")
    fun obtenerTodoLiveData(): LiveData<List<CategoriaPlato>>
    @Query("SELECT * FROM categoria_plato")
     fun obtenerTodo() : List<CategoriaPlato>

    @Query("SELECT * FROM categoria_plato WHERE id = :id")
     fun obtenerPorId(id: String) : CategoriaPlato

    @Insert
     fun guardar(categoriaPlato: CategoriaPlato)

    @Update
     fun actualizar(categoriaPlato: CategoriaPlato)

    @Delete
     fun eliminar(categoriaPlato: CategoriaPlato)
}