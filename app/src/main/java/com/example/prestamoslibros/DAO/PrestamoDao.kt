package com.example.prestamoslibros.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.prestamoslibros.INTERFACES.PrestamoConDetalles
import com.example.prestamoslibros.Model.Prestamo

@Dao

interface PrestamoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prestamo: Prestamo)
    @Query("SELECT * FROM prestamos")
    suspend fun getAllPrestamos(): List<Prestamo>
    @Query("DELETE FROM prestamos WHERE id = :prestamoId")
    suspend fun deleteById(prestamoId: Int): Int
    @Query("""
        SELECT prestamos.fecha_prestamo, prestamos.fecha_devolucion,miembros.nombre 
        AS nombreMiembro, miembros.apellido AS apellidoMiembro,libros.titulo AS tituloLibro        
        FROM prestamos  INNER JOIN miembros ON prestamos.miembroId = miembros.id   
        INNER JOIN libros ON prestamos.libroId = libros.id    
    """)
    suspend fun obtenerPrestamosConDetalles(): List<PrestamoConDetalles>
    @Update    suspend fun update(prestamo: Prestamo): Int}