package com.example.prestamoslibros.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "prestamos",
    foreignKeys = [
        ForeignKey(
            entity = Miembro::class,
            parentColumns = ["id"],
            childColumns = ["miembroId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Libro::class,
            parentColumns = ["id"],
            childColumns = ["libroId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Prestamo (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fecha_prestamo: String,
    val fecha_devolucion: String,
    val miembroId: Int,
    val libroId: Int
)