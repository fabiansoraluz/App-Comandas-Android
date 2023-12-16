package com.example.project_kotlin.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.project_kotlin.entidades.Cargo
import com.example.project_kotlin.entidades.Empleado
import com.example.project_kotlin.entidades.TipoComprobante

@Dao
interface TipoComprobanteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(tipoComprobante: TipoComprobante) : Long
    @Query("select * from Tipo_Comprobante")
    fun obtenerTodo() : List<TipoComprobante>

    @Query("SELECT * FROM Tipo_Comprobante WHERE id = :id")
    fun obtenerPorId(id: Long) : TipoComprobante
}