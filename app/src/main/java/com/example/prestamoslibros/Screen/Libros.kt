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
import com.example.prestamoslibros.INTERFACES.LibroConAutor
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

    var isEditMode by rememberSaveable { mutableStateOf(false) } // Variable para controlar si estamos en modo edición
    var libroId by rememberSaveable { mutableStateOf(0) } // Guardar el ID del libro a editar
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var libroToDelete by rememberSaveable { mutableStateOf<LibroConAutor?>(null) } // Cambiar a LibroConAutor
    var libros by rememberSaveable { mutableStateOf(listOf<LibroConAutor>()) } // Cambiar a LibroConAutor
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
                        id = if (isEditMode) libroId else 0, // Usar el ID si es modo edición, de lo contrario, dejar que Room lo genere
                        titulo = titulo,
                        genero = genero,
                        autorId = selectedAutor!!.id
                    )

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            if (isEditMode) {
                                libroRepository.update(libro) // Actualizar el libro si estamos en modo edición
                                isEditMode = false
                            } else {
                                libroRepository.insert(libro) // Insertar un nuevo libro
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
                                libroId = 0
                            }
                        )
                        libros = withContext(Dispatchers.IO) {
                            libroRepository.getAllLibros() // Obtener los libros con los autores
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
                            libroRepository.getAllLibros() // Obtener los libros con los autores
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
                                Text(text = "Autor: ${libro.nombreAutor} ${libro.apellidoAutor}") // Mostramos el nombre del autor
                            }
                            Row {
                                // Icono para editar
                                IconButton(onClick = {
                                    // Cargar los datos del libro en los campos para editar
                                    titulo = libro.titulo
                                    genero = libro.genero
                                    selectedAutor = autores.firstOrNull { it.id == libro.autorId } // Seleccionar el autor correspondiente
                                    libroId = libro.id // Guardamos el ID del libro para la actualización
                                    isEditMode = true // Cambiar a modo edición
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = Color.Green
                                    )
                                }

                                // Icono para borrar
                                IconButton(onClick = {
                                    libroToDelete = libro // Guardamos el libro a eliminar
                                    showDeleteDialog = true // Mostrar el diálogo de confirmación
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

            // Mostrar el diálogo de confirmación de eliminación
            if (showDeleteDialog && libroToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = "Confirmar Eliminación") },
                    text = { Text(text = "¿Estás seguro de que deseas eliminar este libro?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    libroToDelete?.let { libro ->
                                        withContext(Dispatchers.IO) {
                                            libroRepository.deleteById(libro.id) // Eliminar el libro
                                        }
                                        Toast.makeText(context, "Libro eliminado", Toast.LENGTH_SHORT).show()

                                        libros = withContext(Dispatchers.IO) { // Asegúrate de devolver la lista aquí
                                            libroRepository.getAllLibros() // Actualizar la lista de libros
                                        }
                                    }
                                }
                                showDeleteDialog = false // Cerrar el diálogo
                            }
                        ) {
                            Text(text = "Eliminar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDeleteDialog = false }) {
                            Text(text = "Cancelar")
                        }
                    }
                )
            }
        }
    }
}