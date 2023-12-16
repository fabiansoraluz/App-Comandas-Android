package com.example.project_kotlin.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project_kotlin.entidades.Caja
import com.example.project_kotlin.entidades.MetodoPago
import com.example.project_kotlin.entidades.PlatoConCategoria

@Dao
interface MetodoPagoDao {
    @Query("select * from metodo_pago")
    fun obtenerTodoLiveData(): LiveData<List<MetodoPago>>
    @Query("SELECT * FROM metodo_pago")
    fun buscarTodo() : List<MetodoPago>



    @Query("select * from metodo_pago ")
    fun obtenerTodo():List<MetodoPago>

    @Query("SELECT * FROM metodo_pago WHERE id = :id")
    fun buscarPorId(id: Long) : MetodoPago

    @Query("SELECT * FROM metodo_pago WHERE nombre_metpago = :id")
    fun obtenerPorNombre(id: String) : MetodoPago

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registrar(metodoPago: MetodoPago) : Long

    @Update
    fun actualizar(metodoPago: MetodoPago)

    @Delete
    fun eliminar(metodoPago: MetodoPago)
}