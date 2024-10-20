package com.example.prestamoslibros.Screen

import android.annotation.SuppressLint
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
import com.example.prestamoslibros.Repository.AutorRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AutorApp(autorRepository: AutorRepo) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("") }
    var autorId by rememberSaveable { mutableStateOf(0) } // Guardamos el ID del autor para actualizarlo
    val scope = rememberCoroutineScope()

    var isEditMode by rememberSaveable { mutableStateOf(false) } // Controla si estamos en modo edición
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var autorToDelete by rememberSaveable { mutableStateOf<Autor?>(null) } // Variable para guardar el autor a eliminar
    var autores by rememberSaveable { mutableStateOf(listOf<Autor>()) }

    val context = LocalContext.current

    val navController = rememberNavController() // Crear NavController

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) } // Pasamos el navController a BottomNavigationBar
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campo de entrada para el nombre del autor
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(text = "Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo de entrada para el apellido del autor
            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text(text = "Apellido") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo de entrada para la nacionalidad del autor
            TextField(
                value = nacionalidad,
                onValueChange = { nacionalidad = it },
                label = { Text(text = "Nacionalidad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espacio adicional antes de los botones

            // Botón para registrar o actualizar un autor
            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (apellido.isBlank()) {
                        Toast.makeText(context, "El apellido no puede estar vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (nacionalidad.isBlank()) {
                        Toast.makeText(context, "La nacionalidad no puede estar vacía", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val autor = Autor(
                        id = if (isEditMode) autorId else 0, // Si es modo edición usamos el ID, sino 0 para que Room lo genere
                        nombre = nombre,
                        apellido = apellido,
                        nacionalidad = nacionalidad
                    )

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            if (isEditMode) {
                                autorRepository.update(autor) // Actualizar el autor si estamos en modo edición
                                isEditMode = false
                            } else {
                                autorRepository.insert(autor) // Insertar nuevo autor
                            }
                        }
                        Toast.makeText(
                            context,
                            if (isEditMode) "Autor Actualizado" else "Autor Registrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields(
                            onClear = {
                                nombre = ""
                                apellido = ""
                                nacionalidad = ""
                                autorId = 0
                            }
                        )
                        autores = withContext(Dispatchers.IO) {
                            autorRepository.getAllAutores()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = if (isEditMode) "Actualizar" else "Registrar")
            }

            // Botón para listar los autores
            Button(
                onClick = {
                    scope.launch {
                        autores = withContext(Dispatchers.IO) {
                            autorRepository.getAllAutores()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Listar Autores")
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espacio antes de la lista de autores

            // Scroll para la lista de autores
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f) // Permite que LazyColumn tome el espacio restante y sea desplazable
            ) {
                items(autores.size) { index ->
                    val autor = autores[index]
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
                                Text(text = "ID: ${autor.id}")
                                Text(text = "Nombre: ${autor.nombre}")
                                Text(text = "Apellido: ${autor.apellido}")
                                Text(text = "Nacionalidad: ${autor.nacionalidad}")
                            }
                            Row {
                                // Icono para editar
                                IconButton(onClick = {
                                    // Cargar datos en los campos para editar
                                    nombre = autor.nombre
                                    apellido = autor.apellido
                                    nacionalidad = autor.nacionalidad
                                    autorId = autor.id // Guardamos el ID del autor para la actualización
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
                                    autorToDelete = autor // Guardar el autor a eliminar
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
            if (showDeleteDialog && autorToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = "Confirmar Eliminación") },
                    text = { Text(text = "¿Estás seguro de que deseas eliminar este autor?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    autorToDelete?.let { autor ->
                                        withContext(Dispatchers.IO) {
                                            autorRepository.deleteById(autor.id) // Eliminar el autor
                                        }
                                        Toast.makeText(context, "Autor eliminado", Toast.LENGTH_SHORT).show()
                                        autores = withContext(Dispatchers.IO) {
                                            autorRepository.getAllAutores() // Actualizar la lista
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
