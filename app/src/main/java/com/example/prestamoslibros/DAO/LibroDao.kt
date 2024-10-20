package com.example.prestamoslibros.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.prestamoslibros.Model.Libro

@Dao

interface LibroDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(libro: Libro)
    @Query("SELECT * FROM libros")
    suspend fun getAllLibros(): List<Libro>
    @Update    suspend fun update(libro: Libro): Int
    @Query("DELETE FROM libros WHERE id = :libroId")
    suspend fun deleteById(libroId: Int): Int}