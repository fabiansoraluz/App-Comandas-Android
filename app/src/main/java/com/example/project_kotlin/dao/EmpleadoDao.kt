package com.example.project_kotlin.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.project_kotlin.entidades.Empleado
import com.example.project_kotlin.entidades.EmpleadoUsuarioYCargo
import com.example.project_kotlin.entidades.Plato

@Dao
interface EmpleadoDao {

    @Query("select * from Empleado")
    fun obtenerTodoLiveData(): LiveData<List<EmpleadoUsuarioYCargo>>

    @Query("select * from Empleado")
    fun obtenerTodo(): List<EmpleadoUsuarioYCargo>

    @Query("select * from Empleado where cargo_id = :cargo_id")
    fun buscarPorCargo(cargo_id: String): List<EmpleadoUsuarioYCargo>

    @Query("SELECT * FROM Empleado WHERE id = :id")
    fun obtenerPorId(id: Long) : EmpleadoUsuarioYCargo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(empleado: Empleado) : Long

    @Update
    fun actualizar(empleado: Empleado)

    @Delete
    fun eliminar(empleado: Empleado)
}