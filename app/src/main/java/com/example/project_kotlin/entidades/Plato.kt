package com.example.project_kotlin.entidades

import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "Plato",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaPlato::class,
            parentColumns = ["id"],
            childColumns = ["catplato_id"]
        )
    ],
    indices = [
        Index("catplato_id")
    ])

class Plato (
    @PrimaryKey var id: String = "",
    @NonNull @ColumnInfo(name = "nom_plato") var nombrePlato : String,
    @NonNull @ColumnInfo(name="precio_plato") var precioPlato : Double,
    @NonNull @ColumnInfo(name="nom_imagen") var nombreImagen : String,
    @ColumnInfo(name="catplato_id") var catplato_id : String
    ):java.io.Serializable{

        companion object {
            fun generarCodigo(listaPlatos: List<PlatoConCategoria>): String {
                if (listaPlatos.isEmpty()) return "P-001"

                val ultimoCodigo = listaPlatos.last().plato.id
                val numero = ultimoCodigo.split('-')[1].toInt() + 1

                return "P-${String.format("%03d", numero)}"
            }
        }
    }

data class PlatoConCategoria(
    @Embedded val plato: Plato,
    @Relation(
        parentColumn = "catplato_id",
        entityColumn = "id"
    )
    val categoriaPlato: CategoriaPlato
):java.io.Serializable

data class PlatosConComandas (
    @Embedded val plato : Plato,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_plato"
    )
    val comandas: List<DetalleComanda>
)
