package com.example.checkwork.supportview

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    // Recuperar el estado de modo oscuro de Firebase
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false
            }
        }
    }
    LaunchedEffect(Unit) {
        delay(500)
        isBackButtonEnabled = true
    }

    // Guardar estado del modo oscuro en Firebase
    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soporte y asistencia", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (isBackButtonEnabled) {
                            isBackButtonEnabled = false
                            navController.popBackStack()
                        }
                    }){
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            updateDarkModePreferenceInFirebase(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                    )
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFE0F7FA))
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Contáctanos para asistencia",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkModeEnabled) Color.White else Color(0xFF0056E0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Campo de Nombre con Icono
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo")},
                    textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Nombre Icono", tint = if (isDarkModeEnabled) Color.Black else Color.Black)
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.colors(
                        if (isDarkModeEnabled) Color.Black else Color.Black,
                        if (isDarkModeEnabled) Color.LightGray else Color.Black
                    )
                )

                // Campo de Correo Electrónico con Icono
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico")},
                    textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = "Correo Icono", tint = if (isDarkModeEnabled) Color.Black else Color.Black)
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.colors(
                        if (isDarkModeEnabled) Color.White else Color.Black,
                        if (isDarkModeEnabled) Color.LightGray else Color.Black
                    )
                )

                // Campo de Mensaje con más líneas
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje")},
                    textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 8.dp),
                    maxLines = 5,
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                    if (isDarkModeEnabled) Color.White else Color.Black,
                        if (isDarkModeEnabled) Color.LightGray else Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Enviar estilizado
                Button(
                    onClick = {
                        if (nombre.isNotEmpty() && correo.isNotEmpty() && mensaje.isNotEmpty()) {
                            navController.navigate("dashboard") // Regresar a la pantalla principal
                        } else {
                            error = "Por favor completa todos los campos."
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDarkModeEnabled) Color(0xFF000000)
                    else Color(0xFF0056E0)),
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
