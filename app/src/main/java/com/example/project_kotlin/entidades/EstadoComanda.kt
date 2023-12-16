package com.example.project_kotlin.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EstadosComanda")
class EstadoComanda(
    @PrimaryKey(autoGenerate = true) var id : Long = 0,
    @ColumnInfo(name="estado") var estadoComanda : String
):java.io.Serializable {
}