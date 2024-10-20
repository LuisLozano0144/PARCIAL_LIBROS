package com.example.prestamoslibros.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.prestamoslibros.DAO.AutorDAO
import com.example.prestamoslibros.DAO.LibroDAO
import com.example.prestamoslibros.DAO.MiembroDAO
import com.example.prestamoslibros.DAO.PrestamoDAO
import com.example.prestamoslibros.Model.Autor
import com.example.prestamoslibros.Model.Libro
import com.example.prestamoslibros.Model.Miembro
import com.example.prestamoslibros.Model.Prestamo

@Database(entities = [Autor::class, Libro::class, Miembro::class, Prestamo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Definir todos los DAO
    abstract fun autorDao(): AutorDAO
    abstract fun libroDao(): LibroDAO
    abstract fun miembroDao(): MiembroDAO
    abstract fun prestamoDao(): PrestamoDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}