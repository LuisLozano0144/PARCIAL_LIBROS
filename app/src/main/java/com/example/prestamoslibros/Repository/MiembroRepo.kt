package com.example.prestamoslibros.Repository

import com.example.prestamoslibros.DAO.MiembroDAO
import com.example.prestamoslibros.Model.Miembro

class MiembroRepo (private val miembroDao: MiembroDAO) {
    suspend fun insert(miembro: Miembro) {
        miembroDao.insert(miembro)
    }

    suspend fun getAllMiembros(): List<Miembro> {
        return miembroDao.getAllMiembros()
    }

    suspend fun deleteById(miembroId: Int): Int {
        return miembroDao.deleteById(miembroId)
    }

    suspend fun update(miembro: Miembro): Int {
        return miembroDao.update(miembro)
    }
}