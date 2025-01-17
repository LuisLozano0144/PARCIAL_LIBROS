package com.example.prestamoslibros.Repository

import com.example.prestamoslibros.DAO.LibroDAO
import com.example.prestamoslibros.INTERFACES.LibroConAutor
import com.example.prestamoslibros.Model.Libro

class LibroRepo(private val libroDao: LibroDAO) {
    suspend fun insert(libro: Libro) {
        libroDao.insert(libro)
    }

    suspend fun getAllLibros(): List<LibroConAutor> {
        return libroDao.getAllLibrosConAutores()
    }

    suspend fun deleteById(libroId: Int): Int {
        return libroDao.deleteById(libroId)
    }

    suspend fun update(libro: Libro): Int {
        return libroDao.update(libro)
    }
}