package com.example.project_kotlin.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Establecimiento")
class
Establecimiento(
    @PrimaryKey (autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name="nom_Establecimiento") var nomEstablecimiento: String,
    @ColumnInfo(name="telefo_noestablecimiento") var telefonoestablecimiento: String,
    @ColumnInfo(name="direccio_nestablecimiento") var direccionestablecimiento: String,
    @ColumnInfo(name = "ruc_establecimiento") var rucestablecimiento : String
):java.io.Serializable{
}