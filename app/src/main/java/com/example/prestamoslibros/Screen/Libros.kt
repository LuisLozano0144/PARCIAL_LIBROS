package com.example.prestamoslibros.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.prestamoslibros.BottomNavigationBar
import com.example.prestamoslibros.Model.Autor
import com.example.prestamoslibros.Model.Libro
import com.example.prestamoslibros.Repository.AutorRepo
import com.example.prestamoslibros.Repository.LibroRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LibroApp(libroRepository: LibroRepo, autorRepository: AutorRepo) {
    var titulo by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var selectedAutor by remember { mutableStateOf<Autor?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var isEditMode by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var libroToDelete by rememberSaveable { mutableStateOf<Libro?>(null) }
    var id by rememberSaveable { mutableStateOf("") }
    var libros by rememberSaveable { mutableStateOf(listOf<Libro>()) }
    var autores by remember { mutableStateOf(listOf<Autor>()) }

    val context = LocalContext.current

    // Cargar los autores desde la base de datos
    LaunchedEffect(Unit) {
        autores = withContext(Dispatchers.IO) {
            autorRepository.getAllAutores()
        }
    }

    val navController = rememberNavController() // Crear un NavController

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) } // Pasar el navController a BottomNavigationBar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campo de entrada para el título del libro
            TextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text(text = "Título") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo de entrada para el género del libro
            TextField(
                value = genero,
                onValueChange = { genero = it },
                label = { Text(text = "Género") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Dropdown para seleccionar el autor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                TextField(
                    value = selectedAutor?.nombre ?: "Selecciona un autor",
                    onValueChange = {},
                    label = { Text("Autor") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    trailingIcon = {
                        IconButton(onClick = { isDropdownExpanded = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Seleccionar autor")
                        }
                    }
                )

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    autores.forEach { autor ->
                        DropdownMenuItem(
                            text = { Text(text = "${autor.nombre} ${autor.apellido}") },
                            onClick = {
                                selectedAutor = autor
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para registrar o actualizar un libro
            Button(
                onClick = {
                    if (titulo.isBlank()) {
                        Toast.makeText(context, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (genero.isBlank()) {
                        Toast.makeText(context, "El género no puede estar vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (selectedAutor == null) {
                        Toast.makeText(context, "Debes seleccionar un autor", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val libro = Libro(
                        titulo = titulo,
                        genero = genero,
                        autorId = selectedAutor!!.id
                    )

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            if (isEditMode) {
                                libroRepository.update(libro)
                                isEditMode = false
                            } else {
                                libroRepository.insert(libro)
                            }
                        }
                        Toast.makeText(
                            context,
                            if (isEditMode) "Libro Actualizado" else "Libro Registrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields(
                            onClear = {
                                titulo = ""
                                genero = ""
                                selectedAutor = null
                                id = ""
                            }
                        )
                        libros = withContext(Dispatchers.IO) {
                            libroRepository.getAllLibros()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = if (isEditMode) "Actualizar" else "Registrar")
            }

            // Botón para listar los libros
            Button(
                onClick = {
                    scope.launch {
                        libros = withContext(Dispatchers.IO) {
                            libroRepository.getAllLibros()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Listar Libros")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scroll para la lista de libros
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f) // Permite que LazyColumn tome el espacio restante y sea desplazable
            ) {
                items(libros.size) { index ->
                    val libro = libros[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "ID: ${libro.id}")
                                Text(text = "Título: ${libro.titulo}")
                                Text(text = "Género: ${libro.genero}")
                                Text(text = "ID del Autor: ${libro.autorId}")
                            }
                            Row {
                                // Icono para editar
                                IconButton(onClick = {
                                    titulo = libro.titulo
                                    genero = libro.genero
                                    selectedAutor = autores.firstOrNull { it.id == libro.autorId }
                                    id = libro.id.toString()
                                    isEditMode = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Color.Green
                                    )
                                }

                                // Icono para borrar
                                IconButton(onClick = {
                                    libroToDelete = libro
                                    showDeleteDialog = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Borrar",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

