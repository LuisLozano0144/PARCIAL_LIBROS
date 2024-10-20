package com.example.prestamoslibros.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.prestamoslibros.Model.Miembro

@Dao

interface MiembroDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(miembro: Miembro)
    @Query("SELECT * FROM miembros")
    suspend fun getAllMiembros(): List<Miembro>
    @Query ("DELETE FROM miembros WHERE id = :miembroId")
    suspend fun deleteById(miembroId: Int): Int    @Update
    suspend fun update(miembro: Miembro): Int}