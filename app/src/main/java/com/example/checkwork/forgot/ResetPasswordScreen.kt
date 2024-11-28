import android.annotation.SuppressLint
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showValidationErrorDialog by remember { mutableStateOf(false) }
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    // URL del GIF específico
    val gifUrl = "https://media.giphy.com/media/IgLIVXrBcID9cExa6r/giphy.gif"

    // Evitar múltiples pulsaciones rápidas
    fun safelyNavigateBack() {
        if (isBackButtonEnabled) {
            isBackButtonEnabled = false
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        "Restablecer contraseña",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0056E0) // Color azul fuerte
                ),
                navigationIcon = {
                    IconButton(onClick = { safelyNavigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE3F2FD)) // Fondo claro
                    .padding(55.dp), // Margen uniforme
                verticalArrangement = Arrangement.Top, // Coloca los elementos desde arriba
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el TopAppBar y el GIF
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current)
                            .data(gifUrl)
                            .decoderFactory(ImageDecoderDecoder.Factory()) // Animación del GIF
                            .size(Size.ORIGINAL)
                            .build()
                    ),
                    contentDescription = "Forgot Password GIF",
                    modifier = Modifier
                        .size(150.dp) // Tamaño del GIF
                        .clip(CircleShape) // Forma redonda
                        .background(Color.White) // Fondo blanco
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el GIF y la primera Card

                // Card para introducir correo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Introduce tu correo electrónico",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Correo Electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.isNotEmpty()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    showValidationErrorDialog = true
                                } else {
                                    FirebaseAuth.getInstance()
                                        .sendPasswordResetEmail(email)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                showSuccessDialog = true
                                            } else {
                                                errorMessage = task.exception?.message ?: "Ocurrió un error"
                                            }
                                        }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0056E0))
                        ) {
                            Text("Enviar Correo", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        errorMessage?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre las Cards

                // Card con instrucciones para restablecer la contraseña
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "Instrucciones para restablecer la contraseña:",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "- Ingresa tu correo electrónico registrado.",
                            fontSize = 16.sp
                        )
                        Text(
                            "- Presiona 'Enviar Correo'.",
                            fontSize = 16.sp
                        )
                        Text(
                            "- Revisa tu bandeja de entrada y sigue las instrucciones del correo.",
                            fontSize = 16.sp
                        )
                    }
                }

                if (showValidationErrorDialog) {
                    AlertDialog(
                        onDismissRequest = { showValidationErrorDialog = false },
                        title = { Text("Error") },
                        text = { Text("Por favor, introduce un correo válido.") },
                        confirmButton = {
                            TextButton(onClick = { showValidationErrorDialog = false }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }

                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { },
                        title = { Text("¡Éxito!") },
                        text = { Text("Se ha enviado un correo para restablecer tu contraseña.") },
                        confirmButton = {
                            TextButton(onClick = {
                                showSuccessDialog = false
                                navController.navigate("login")
                            }) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
            }
        }
    )
}
