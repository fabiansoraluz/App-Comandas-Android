package com.example.project_kotlin.entidades.relaciones

import androidx.room.Embedded
import androidx.room.Relation
import com.example.project_kotlin.entidades.Comanda
import com.example.project_kotlin.entidades.Mesa

class MesaConComandas (
    @Embedded val mesa : Mesa,
    @Relation(
        parentColumn = "id",
        entityColumn = "mesa_id"
    )
    val comandas: List<Comanda>
)