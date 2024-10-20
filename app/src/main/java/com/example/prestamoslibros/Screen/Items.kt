package com.example.prestamoslibros.Screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.prestamoslibros.BottomNavigationBar
import com.example.prestamoslibros.Model.Miembro
import com.example.prestamoslibros.Repository.MiembroRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")


@Composable
fun MiembroApp(miembroRepository: MiembroRepo) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var fechaInscripcion by remember { mutableStateOf("") }
    var miembroId by rememberSaveable { mutableStateOf(0) } // Guardar el ID del miembro para editar
    val scope = rememberCoroutineScope()

    var isEditMode by rememberSaveable { mutableStateOf(false) } // Controla si estamos en modo edición
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var miembroToDelete by rememberSaveable { mutableStateOf<Miembro?>(null) } // Variable para guardar el miembro a eliminar
    var miembros by rememberSaveable { mutableStateOf(listOf<Miembro>()) }

    val context = LocalContext.current

    // Formatear la fecha a un formato más legible
    val dateFormatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campo de entrada para el nombre del miembro
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(text = "Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo de entrada para el apellido del miembro
            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text(text = "Apellido") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Campo de entrada para la fecha de inscripción del miembro
            TextField(
                value = fechaInscripcion,
                onValueChange = { fechaInscripcion = it },
                label = { Text(text = "Fecha de Inscripción (yyyy-MM-dd)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para registrar o actualizar un miembro
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

                    if (fechaInscripcion.isBlank()) {
                        Toast.makeText(context, "La fecha de inscripción no puede estar vacía", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val miembro = Miembro(
                        id = if (isEditMode) miembroId else 0,
                        nombre = nombre,
                        apellido = apellido,
                        fecha_inscripcion = fechaInscripcion
                    )

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            if (isEditMode) {
                                miembroRepository.update(miembro)
                                isEditMode = false
                            } else {
                                miembroRepository.insert(miembro)
                            }
                        }
                        Toast.makeText(
                            context,
                            if (isEditMode) "Miembro Actualizado" else "Miembro Registrado",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields(
                            onClear = {
                                nombre = ""
                                apellido = ""
                                fechaInscripcion = ""
                                miembroId = 0
                            }
                        )
                        miembros = withContext(Dispatchers.IO) {
                            miembroRepository.getAllMiembros()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = if (isEditMode) "Actualizar" else "Registrar")
            }

            // Botón para listar los miembros
            Button(
                onClick = {
                    scope.launch {
                        miembros = withContext(Dispatchers.IO) {
                            miembroRepository.getAllMiembros()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "Listar Miembros")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scroll para la lista de miembros
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                items(miembros.size) { index ->
                    val miembro = miembros[index]

                    // Parsear la fecha de inscripción para darle formato legible
                    val formattedDate = try {
                        val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(miembro.fecha_inscripcion)
                        parsedDate?.let { dateFormatter.format(it) } ?: miembro.fecha_inscripcion
                    } catch (e: Exception) {
                        miembro.fecha_inscripcion // Si hay un error al parsear, mostrar la fecha original
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column( // Cambiamos Row por Column
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween // Espaciado entre el texto y los iconos
                        ) {
                            // Texto con los datos del miembro
                            Column {
                                Text(text = "ID: ${miembro.id}")
                                Text(text = "Nombre: ${miembro.nombre}")
                                Text(text = "Apellido: ${miembro.apellido}")
                                Text(text = "Fecha de Inscripción: $formattedDate")
                            }
                            Spacer(modifier = Modifier.height(8.dp)) // Añadir espacio antes de los iconos

                            // Row para los iconos de editar y borrar
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Icono para editar
                                IconButton(onClick = {
                                    nombre = miembro.nombre
                                    apellido = miembro.apellido
                                    fechaInscripcion = miembro.fecha_inscripcion
                                    miembroId = miembro.id
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
                                    miembroToDelete = miembro
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

            if (showDeleteDialog && miembroToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = "Confirmar Eliminación") },
                    text = { Text(text = "¿Estás seguro de que deseas eliminar este miembro?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                scope.launch {
                                    miembroToDelete?.let { miembro ->
                                        withContext(Dispatchers.IO) {
                                            miembroRepository.deleteById(miembro.id)
                                        }
                                        Toast.makeText(context, "Miembro eliminado", Toast.LENGTH_SHORT).show()
                                        miembros = withContext(Dispatchers.IO) {
                                            miembroRepository.getAllMiembros()
                                        }
                                    }
                                }
                                showDeleteDialog = false
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

fun clearFields(onClear: () -> Unit) {
    onClear()
}