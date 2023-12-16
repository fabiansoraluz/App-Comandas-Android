package com.example.project_kotlin.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project_kotlin.entidades.Comanda
import com.example.project_kotlin.entidades.ComandaMesaYEmpleadoYEstadoComanda

@Dao
interface ComandaDao {
    @Transaction
    @Query("SELECT * FROM comanda")
     fun obtenerTodo() : LiveData<List<ComandaMesaYEmpleadoYEstadoComanda>>

    @Query("SELECT * FROM comanda WHERE id = :id")
     fun obtenerPorId(id: Long) : ComandaMesaYEmpleadoYEstadoComanda

    @Query("SELECT * FROM comanda WHERE estado_comanda_id != 2")
    fun ComandasSinPagar() : List<ComandaMesaYEmpleadoYEstadoComanda>

    @Query("SELECT * FROM comanda WHERE empleado_id = :id")
    fun ComandasDeEmpleado(id: Int) : List<Comanda>

     @Query("SELECT * FROM comanda WHERE mesa_id = :mesa_id")
     fun obtenerComandasPorMesa(mesa_id: Int) : List<ComandaMesaYEmpleadoYEstadoComanda>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun guardar(comanda: Comanda) : Long

    @Update
     fun actualizar(comanda: Comanda)

    @Delete
     fun eliminar(comanda: Comanda)
}