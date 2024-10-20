package com.example.prestamoslibros.INTERFACES

data class PrestamoConDetalles(
    val fecha_prestamo: String,
    val fecha_devolucion: String,
    val nombreMiembro: String,
    val apellidoMiembro: String,
    val tituloLibro: String)