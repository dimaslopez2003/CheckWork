package com.example.checkwork.Home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaRegistro(navController: NavHostController, username: String?) {

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFF0056E0))

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // Firestore instance and user ID
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var comentario by remember { mutableStateOf("") }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        drawerContent = {
            // Contenido del menú lateral (Hamburger Menu)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF042159))
                    .padding(16.dp)
            ) {
                Text(
                    text = "SISTEMAS",
                    style = MaterialTheme.typography.h6.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "USERNAME",
                    style = MaterialTheme.typography.body1.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Perfil", color = Color.White, fontSize = 16.sp)
                        Text(text = "Agrega una foto para identificarte.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                // Registrar biométricos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = "Registrar biométricos",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Registrar biométricos", color = Color.White, fontSize = 16.sp)
                        Text(text = "Habilitar Iniciar sesión con tu huella dactilar.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.3f))

                // Modo Oscuro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Brightness2,
                        contentDescription = "Modo Oscuro",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Modo Oscuro", color = Color.White, fontSize = 16.sp)
                        Text(text = "Para descansar tu vista activa modo oscuro.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                // Asistencia
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Help,
                        contentDescription = "Asistencia",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Asistencia", color = Color.White, fontSize = 16.sp)
                        Text(text = "Soporte, ayuda y más.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.3f))

                // Navegación al formulario
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            coroutineScope.launch { scaffoldState.drawerState.close() }
                            navController.navigate("form")
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Formulario",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Formulario", color = Color.White, fontSize = 16.sp)
                }

                // Cerrar sesión
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            coroutineScope.launch { scaffoldState.drawerState.close() }
                            navController.navigate("login")
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Cerrar Sesión", color = Color.White, fontSize = 16.sp)
                }
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡BIENVENIDO, $username!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para comentario
                TextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentarios") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de Entrada y Salida
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f).padding(8.dp),
                        onClick = {
                            coroutineScope.launch {
                                if (userId != null) {
                                    registrarEntradaSalida(
                                        db = db,
                                        userId = userId,
                                        tipo = "entrada",
                                        comentario = comentario
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50))
                    ) {
                        Text("Registrar Entrada")
                    }

                    Button(
                        modifier = Modifier.weight(1f).padding(8.dp),
                        onClick = {
                            coroutineScope.launch {
                                if (userId != null) {
                                    registrarEntradaSalida(
                                        db = db,
                                        userId = userId,
                                        tipo = "salida",
                                        comentario = comentario
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336))
                    ) {
                        Text("Registrar Salida")
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    )
}

fun registrarEntradaSalida(
    db: FirebaseFirestore,
    userId: String,
    tipo: String,
    comentario: String
) {
    val data = hashMapOf(
        "tipo" to tipo,
        "timestamp" to System.currentTimeMillis(),
        "comentario" to comentario
    )

    db.collection("usuarios")
        .document(userId)
        .collection("registros")
        .add(data)
        .addOnSuccessListener {
            println("Registro de $tipo guardado exitosamente.")
        }
        .addOnFailureListener { e ->
            println("Error al guardar registro de $tipo: ${e.message}")
        }
}
