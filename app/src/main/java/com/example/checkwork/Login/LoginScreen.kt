package com.example.checkwork.Login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.checkwork.R
import com.example.checkwork.data.model.AuthManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val authManager = AuthManager()
    val db = FirebaseFirestore.getInstance()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.load))
    val scope = rememberCoroutineScope()

    if (isLoading) {
        // Pantalla de animación de carga
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(150.dp)
            )
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { Text("WorkCheckApp", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0056E0)
                    )
                )
            },
            content = {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    color = Color(0xFFE0F7FA)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "¡Bienvenido a WorkCheckApp!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color(0xFF000000)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it.replace(" ", "").replace("\n", "")
                                emailError = false
                            },
                            label = { Text(text = "Email") },
                            placeholder = { Text(text = "Example@gmail.com") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = emailError,
                            textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        )
                        if (emailError) {
                            Text(
                                text = "El email es obligatorio",
                                color = Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it.replace(" ", "").replace("\n", "")
                                passwordError = false
                            },
                            label = { Text(text = "Password") },
                            placeholder = { Text(text = "At least 8 characters") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF000000)
                                    )
                                }
                            },
                            isError = passwordError,
                            textStyle = LocalTextStyle.current.copy(color = Color.Black)
                        )
                        if (passwordError) {
                            Text(
                                text = "La contraseña es obligatoria",
                                color = Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = { navController.navigate("reset_password") }) {
                            Text(
                                text = "¿Olvidaste tu contraseña\uD83E\uDD14?",
                                color = Color(0xFF000000),
                                textDecoration = TextDecoration.Underline,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF000000)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                emailError = email.isEmpty()
                                passwordError = password.isEmpty()

                                if (!emailError && !passwordError) {
                                    scope.launch {
                                        isLoading = true // Mostrar animación de carga al iniciar la operación
                                        authManager.signInWithEmailAndPassword(email, password) { success, error ->
                                            isLoading = false // Ocultar animación de carga al finalizar la operación
                                            if (success) {
                                                navController.navigate("home") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            } else {
                                                // Mostrar diálogo de error sin pasar por la animación
                                                errorMessage = error ?: "Correo o contraseña incorrectos"
                                                showDialog = true
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0056E0),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Iniciar sesión")
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { navController.navigate("register") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0056E0),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Registro", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                confirmButton = {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("OK")
                                    }
                                },
                                title = { Text(text = "Error de inicio de sesión") },
                                text = { Text(text = errorMessage) }
                            )
                        }
                    }
                }
            }
        )
    }
}
