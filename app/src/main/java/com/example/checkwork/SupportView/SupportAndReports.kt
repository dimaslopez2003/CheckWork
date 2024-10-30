package com.example.checkwork.supportview // Cambiado a minúsculas

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable



fun SoporteScreen(navController: NavHostController) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }


    Scaffold(
            topBar = {
                androidx.compose.material.TopAppBar(
                    title = { androidx.compose.material.Text("Soporte y asistencia", color = Color.White) },
                    backgroundColor = Color(0xFF0056E0),
                    navigationIcon = {
                        androidx.compose.material.IconButton(onClick = { navController.popBackStack() }) {
                            androidx.compose.material.Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Regresar",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA))
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Contáctanos para asistencia",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0056E0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Campo de Nombre con Icono
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Nombre Icono")
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),

                )

                // Campo de Correo Electrónico con Icono
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = "Correo Icono")
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                // Campo de Mensaje con más líneas
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 8.dp),
                    maxLines = 5,
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Enviar estilizado
                Button(
                    onClick = {
                        if (nombre.isNotEmpty() && correo.isNotEmpty() && mensaje.isNotEmpty()) {
                            // Lógica de envío del mensaje
                            navController.navigate("dashboard") // Regresar a la pantalla principal
                        } else {
                            error = "Por favor completa todos los campos."
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0056E0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Enviar", color = Color.White, fontWeight = FontWeight.Bold)
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}
