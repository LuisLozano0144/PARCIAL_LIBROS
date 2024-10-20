package com.example.prestamoslibros.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName =  "miembros")
data class Miembro (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val fecha_inscripcion: String,
)