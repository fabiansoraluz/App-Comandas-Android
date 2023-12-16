package com.example.project_kotlin.entidades

import androidx.room.*

@Entity(tableName = "cargo")
data class Cargo(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var cargo: String):java.io.Serializable {
}