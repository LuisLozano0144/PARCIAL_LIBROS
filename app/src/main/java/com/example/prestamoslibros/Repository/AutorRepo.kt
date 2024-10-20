package com.example.prestamoslibros.Repository

import com.example.prestamoslibros.DAO.AutorDAO
import com.example.prestamoslibros.Model.Autor

class AutorRepo (private val autorDao: AutorDAO) {
    suspend fun insert(autor: Autor) {
        autorDao.insert(autor)
    }

    suspend fun getAllAutores(): List<Autor> {
        return autorDao.getAllAutores()
    }

    suspend fun deleteById(autorId: Int): Int {
        return autorDao.deleteById(autorId)
    }

    suspend fun update(autor: Autor): Int {
        return autorDao.update(autor)
    }
}