package com.example.project_kotlin.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tipo_Comprobante")
class TipoComprobante(
    @PrimaryKey(autoGenerate = true) var id : Long = 0,
    @ColumnInfo(name="nombre_comprobante") var tipo : String
):java.io.Serializable