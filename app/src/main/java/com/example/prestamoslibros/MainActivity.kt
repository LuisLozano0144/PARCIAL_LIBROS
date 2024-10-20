package com.example.prestamoslibros
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.prestamoslibros.DataBase.AppDatabase
import com.example.prestamoslibros.Repository.AutorRepo
import com.example.prestamoslibros.Repository.LibroRepo
import com.example.prestamoslibros.Repository.MiembroRepo
import com.example.prestamoslibros.Repository.PrestamoRepo
import com.example.prestamoslibros.Screen.AutorApp
import com.example.prestamoslibros.Screen.LibroApp
import com.example.prestamoslibros.Screen.MiembroApp


class MainActivity : ComponentActivity() {

    // Repositorios
    private lateinit var autorRepository: AutorRepo
    private lateinit var libroRepository: LibroRepo
    private lateinit var miembroRepository: MiembroRepo
    private lateinit var prestamoRepository: PrestamoRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos la base de datos y los DAO
        val db = AppDatabase.getDatabase(applicationContext)

        // Inicializamos los DAO
        val autorDAO = db.autorDao()
        val libroDAO = db.libroDao()
        val miembroDAO = db.miembroDao()
        val prestamoDAO = db.prestamoDao()

        // Inicializamos los repositorios
        autorRepository = AutorRepo(autorDAO)
        libroRepository = LibroRepo(libroDAO)
        miembroRepository = MiembroRepo(miembroDAO)
        prestamoRepository = PrestamoRepo(prestamoDAO)

        enableEdgeToEdge()

        //setContent {
          //  MiembroApp(miembroRepository)
        //}

        // Configuramos el contenido usando Jetpack Compose
        setContent {
            MyApp(
                autorRepository = autorRepository,
                libroRepository = libroRepository,
                miembroRepository = miembroRepository,
                //prestamoRepository = prestamoRepository
            )
        }
    }
}


@Composable
fun NavigationHost(
    navController: NavHostController,
    autorRepository: AutorRepo,
    libroRepository: LibroRepo,
    miembroRepository: MiembroRepo
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Autores.route
    ) {
        composable(BottomNavItem.Autores.route) {
            AutorApp(autorRepository)
        }
        composable(BottomNavItem.Libros.route) {
            LibroApp(libroRepository, autorRepository)
        }
        composable(BottomNavItem.Miembros.route) {
            MiembroApp(miembroRepository)
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp(
    autorRepository: AutorRepo,
    libroRepository: LibroRepo,
    miembroRepository: MiembroRepo
) {
    val navController = rememberNavController() // Controlador de navegación

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) } // Barra de navegación inferior
    ) {
        NavigationHost(
            navController = navController,
            autorRepository = autorRepository,
            libroRepository = libroRepository,
            miembroRepository = miembroRepository
        )
    }
}

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Autores : BottomNavItem("Autores", Icons.Default.Person, "autores")
    object Libros : BottomNavItem("Libros", Icons.Default.Book, "libros")
    object Miembros : BottomNavItem("Miembros", Icons.Default.People, "miembros")
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Autores,
        BottomNavItem.Libros,
        BottomNavItem.Miembros
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = false, // Aquí puedes manejar el estado seleccionado si lo deseas
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}