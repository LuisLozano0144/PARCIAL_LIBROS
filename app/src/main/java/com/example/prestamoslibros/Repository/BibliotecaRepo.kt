package com.example.prestamoslibros.Repository

import com.example.prestamoslibros.DAO.AutorDAO
import com.example.prestamoslibros.DAO.LibroDAO
import com.example.prestamoslibros.DAO.MiembroDAO
import com.example.prestamoslibros.DAO.PrestamoDAO
import com.example.prestamoslibros.INTERFACES.PrestamoConDetalles
import com.example.prestamoslibros.Model.Autor
import com.example.prestamoslibros.Model.Libro
import com.example.prestamoslibros.Model.Miembro
import com.example.prestamoslibros.Model.Prestamo

class BibliotecaRepo (
    private val autorDao: AutorDAO,
    private val libroDao: LibroDAO,
    private val miembroDao: MiembroDAO,
    private val prestamoDao: PrestamoDAO
) {
    // Operaciones para Autores
    suspend fun insertAutor(autor: Autor) {
        autorDao.insert(autor)
    }

    suspend fun getAllAutores(): List<Autor> {
        return autorDao.getAllAutores()
    }

    suspend fun deleteAutorById(autorId: Int): Int {
        return autorDao.deleteById(autorId)
    }

    // Operaciones para Libros
    suspend fun insertLibro(libro: Libro) {
        libroDao.insert(libro)
    }

    suspend fun getAllLibros(): List<Libro> {
        return libroDao.getAllLibros()
    }

    // Operaciones para Miembros
    suspend fun insertMiembro(miembro: Miembro) {
        miembroDao.insert(miembro)
    }

    suspend fun getAllMiembros(): List<Miembro> {
        return miembroDao.getAllMiembros()
    }

    // Operaciones para Préstamos
    suspend fun insertPrestamo(prestamo: Prestamo) {
        prestamoDao.insert(prestamo)
    }

    suspend fun obtenerPrestamosConDetalles(): List<PrestamoConDetalles> {
        return prestamoDao.obtenerPrestamosConDetalles()
    }
}