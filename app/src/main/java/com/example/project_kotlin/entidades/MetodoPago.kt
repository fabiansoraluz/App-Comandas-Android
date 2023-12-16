package com.example.project_kotlin.entidades

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metodo_pago")
class MetodoPago(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @NonNull @ColumnInfo(name = "nombre_metpago") var nombreMetodoPago: String):java.io.Serializable {
}