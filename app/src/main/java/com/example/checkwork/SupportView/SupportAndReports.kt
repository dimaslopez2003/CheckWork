package com.example.checkwork.supportview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.checkwork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteScreen(navController: NavHostController) {
    var nombre by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isBackButtonEnabled by remember { mutableStateOf(true) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        delay(500)
        isBackButtonEnabled = true
    }

    // Cargar animaciones de Lottie
    val supportComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.support))
    val loadingComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.load))
    val progress by animateLottieCompositionAsState(loadingComposition)

    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Soporte", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (isBackButtonEnabled) {
                            isBackButtonEnabled = false
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Interruptor de modo oscuro en la barra superior
                    androidx.compose.material.Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            updateDarkModePreferenceInFirebase(it) // Guardar el estado en Firebase
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                    )
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Contáctanos para asistencia",
                    fontSize = 24.sp,
                    color = Color(0xFF0056E0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Animación de soporte (avatar)
                LottieAnimation(
                    composition = supportComposition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(150.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    // Mostrar animación de carga con un retraso para que sea visible
                    LaunchedEffect(isLoading) {
                        delay(3000)
                    }
                    LottieAnimation(
                        composition = loadingComposition,
                        progress = progress,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            if (nombre.isNotEmpty() && mensaje.isNotEmpty()) {
                                isLoading = true
                                scope.launch {
                                    delay(2000)
                                    sendEmail(context, nombre, mensaje)
                                    isLoading = false
                                }
                            } else {
                                error = "Por favor completa todos los campos."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkModeEnabled) Color(0xFF000000) else Color(0xFF0056E0),
                            contentColor = if (isDarkModeEnabled) Color(0xFFFFFFFF) else Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = "Enviar correo", color = Color.White)
                    }
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}

fun sendEmail(context: Context, nombre: String, mensaje: String) {
    try {
        // Crear un Intent explícito para aplicaciones de correo
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // Asegura que se abra una app de correo
            putExtra(Intent.EXTRA_EMAIL, arrayOf("montalcoarturo@gmail.com")) // Correo destino
            putExtra(Intent.EXTRA_SUBJECT, "Reporte de Soporte")
            putExtra(
                Intent.EXTRA_TEXT,
                "Nombre: $nombre\n\nMensaje:\n$mensaje"
            )
        }

        // Verificar si hay una aplicación que pueda manejar este Intent
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "Selecciona una aplicación de correo"))
        } else {
            // Mostrar un mensaje si no hay apps compatibles
            Toast.makeText(
                context,
                "No se encontró una aplicación de correo instalada.",
                Toast.LENGTH_LONG
            ).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            context,
            "Hubo un problema al intentar enviar el correo.",
            Toast.LENGTH_LONG
        ).show()
    }
}
