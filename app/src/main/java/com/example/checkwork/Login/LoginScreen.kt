package com.example.checkwork.Login.LoginScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.navigation.NavHostController
import com.example.checkwork.data.model.AuthManager

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
    val authManager = AuthManager()

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
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bienvenido a WorkCheckApp",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = false
                        },
                        label = { Text(text = "Email") },
                        placeholder = { Text(text = "Example@email.com") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = emailError
                    )
                    if (emailError) {
                        Text(text = "El email es obligatorio", color = Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
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
                                Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                            }
                        },
                        isError = passwordError
                    )
                    if (passwordError) {
                        Text(text = "La contraseña es obligatoria", color = Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = {}) {
                        Text(text = "Forgot Password?", color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón de inicio de sesión
                    Button(
                        onClick = {
                            // Validar si los campos están vacíos
                            emailError = email.isEmpty()
                            passwordError = password.isEmpty()

                            if (!emailError && !passwordError) {
                                authManager.signInWithEmailAndPassword(email, password) { success, error ->
                                    if (success) {
                                        navController.navigate("home")
                                    } else {
                                        errorMessage = error ?: "Correo o contraseña incorrectos"
                                        showDialog = true
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

                    // Botón de registro
                    OutlinedButton(
                        onClick = { navController.navigate("register") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text(text = "Registro", color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostrar dialogo de error en caso de fallo
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