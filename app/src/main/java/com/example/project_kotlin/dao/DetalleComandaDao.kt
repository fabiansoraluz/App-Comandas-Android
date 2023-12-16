package com.example.project_kotlin.dao

import androidx.room.*
import com.example.project_kotlin.entidades.DetalleComanda
import com.example.project_kotlin.entidades.DetalleComandaConPlato
import com.example.project_kotlin.entidades.Empleado

@Dao
interface DetalleComandaDao {
    @Query("select * from Detalle_Comanda")
    fun obtenerTodo(): List<DetalleComandaConPlato>

    @Query("select * from Detalle_Comanda where comanda_id = :comanda_id")
    fun buscarDetallesPorComanda(comanda_id: Long): List<DetalleComandaConPlato>

    @Query("SELECT * FROM Detalle_Comanda WHERE comanda_id = :comanda_id AND id_plato = :id_plato")
    fun obtenerPorComandaYPlato(comanda_id: Long, id_plato: String) : DetalleComandaConPlato

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(detalleComanda: DetalleComanda) : Long

    @Update
    fun actualizar(detalleComanda: DetalleComanda)

    @Delete
    fun eliminar(detalleComanda: DetalleComanda)
}