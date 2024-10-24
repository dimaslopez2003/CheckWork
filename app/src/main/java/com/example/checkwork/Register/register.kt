package com.example.checkwork.Register.register

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.checkwork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Empleado") } // El rol seleccionado

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Registro", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo de la app",
                    modifier = Modifier.size(120.dp)
                )
                Text(
                    text = "Crear una cuenta",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(
                                imageVector = image,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Selecciona tu rol:", fontSize = 18.sp, color = Color(0xFF000000))
                Spacer(modifier = Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color(0xFF0056E0),
                            contentColor = Color.White // Color del texto dentro del botón
                        )
                    ) {
                        Text(selectedRole)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownMenuItem(onClick = {
                            selectedRole = "Administrador"
                            expanded = false
                        }) {
                            Text("Administrador")
                        }
                        DropdownMenuItem(onClick = {
                            selectedRole = "Empleado"
                            expanded = false
                        }) {
                            Text("Empleado")
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0056E0)),
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                            coroutineScope.launch {
                                try {
                                    auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val user = auth.currentUser
                                                val userId = user?.uid

                                                if (userId != null) {
                                                    val userMap = hashMapOf(
                                                        "username" to username,
                                                        "email" to email,
                                                        "rol" to selectedRole
                                                    )

                                                    db.collection("users")
                                                        .document(userId)
                                                        .set(userMap)
                                                        .addOnSuccessListener {
                                                            if (selectedRole == "Administrador") {
                                                                // Generar código de empresa si es administrador
                                                                val companyCode =
                                                                    generateCompanyCode()
                                                                val companyMap = hashMapOf(
                                                                    "companyCode" to companyCode
                                                                )
                                                                db.collection("empresa")
                                                                    .document(userId)
                                                                    .set(companyMap)
                                                                    .addOnSuccessListener {
                                                                        coroutineScope.launch {
                                                                            scaffoldState.snackbarHostState.showSnackbar(
                                                                                "Registro exitoso. Código de empresa: $companyCode"
                                                                            )
                                                                            navController.navigate("login")
                                                                        }
                                                                    }
                                                                    .addOnFailureListener {
                                                                        errorMessage =
                                                                            "Error al generar el código de empresa"
                                                                    }
                                                            } else {
                                                                coroutineScope.launch {
                                                                    scaffoldState.snackbarHostState.showSnackbar(
                                                                        "Registro exitoso"
                                                                    )
                                                                    navController.navigate("login")
                                                                }
                                                            }
                                                        }
                                                        .addOnFailureListener {
                                                            errorMessage =
                                                                "Error al guardar el usuario"
                                                        }
                                                }
                                            } else {
                                                errorMessage =
                                                    task.exception?.message ?: "Error al registrar"
                                            }
                                        }
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Error desconocido"
                                }
                            }
                        } else {
                            errorMessage = "Por favor completa todos los campos"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse", color = Color.White)
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = errorMessage, color = Color.Red)
                }
            }

        }
    )
}

// Función para generar un código de empresa aleatorio
fun generateCompanyCode(): String {
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..8)
        .map { characters.random() }
        .joinToString("")
}
