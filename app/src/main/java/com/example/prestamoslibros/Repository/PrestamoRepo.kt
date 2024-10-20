package com.example.prestamoslibros.Repository

import com.example.prestamoslibros.DAO.PrestamoDAO
import com.example.prestamoslibros.INTERFACES.PrestamoConDetalles
import com.example.prestamoslibros.Model.Prestamo

class PrestamoRepo (private val prestamoDao: PrestamoDAO) {
    suspend fun insert(prestamo: Prestamo) {
        prestamoDao.insert(prestamo)
    }

    suspend fun getAllPrestamos(): List<Prestamo> {
        return prestamoDao.getAllPrestamos()
    }

    suspend fun deleteById(prestamoId: Int): Int {
        return prestamoDao.deleteById(prestamoId)
    }

    suspend fun update(prestamo: Prestamo): Int {
        return prestamoDao.update(prestamo)
    }

    suspend fun obtenerPrestamosConDetalles(): List<PrestamoConDetalles> {
        return prestamoDao.obtenerPrestamosConDetalles()
    }
}