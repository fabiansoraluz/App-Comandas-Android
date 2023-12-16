package com.example.project_kotlin.entidades

import androidx.room.*

@Entity(tableName = "mesa")
data class Mesa (
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "cantidad_asientos") var cantidadAsientos: Int,
    @ColumnInfo(name = "estado_mesa", defaultValue = "Libre") var estado: String? = null):java.io.Serializable {
}

