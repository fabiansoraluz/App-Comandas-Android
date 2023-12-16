package com.example.project_kotlin.entidades

import androidx.room.*

@Entity(tableName = "caja",
    foreignKeys = [ForeignKey(
        entity = Establecimiento::class,
        parentColumns = ["id"],
        childColumns = ["establecimiento_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("establecimiento_id")
    ])
data class Caja(
    @PrimaryKey
    var id: String = ""
) :java.io.Serializable{
    @Embedded(prefix = "establecimiento_")
    var establecimiento: Establecimiento? = null

    constructor(listaCaja: List<Caja>) : this() {
        id = generarIdCaja(listaCaja)
    }

    companion object {
        fun generarIdCaja(listaCaja: List<Caja>): String {
            if (listaCaja.isEmpty())
                return "C-001"

            val ultimoId = listaCaja[listaCaja.size - 1].id

            val numero = Integer.parseInt(ultimoId.split("-")[1])

            return "C-" + String.format("%03d", numero + 1)
        }
    }
}



data class CajaConComprobantes(
    @Embedded val caja: Caja,
    @Relation(
        parentColumn = "id",
        entityColumn = "caja_id"
    )
    val comprobantes: List<Comprobante>
)