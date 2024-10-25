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
import com.example.checkwork.FunctionTime.getCurrentTime
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal(navController: NavHostController, username: String?) {

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFF0056E0))

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var username by remember { mutableStateOf("") }

    var currentTime by remember { mutableStateOf(getCurrentTime()) }


    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

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
                            Icons.Filled.Brightness2,
                            contentDescription = "DarkMode",
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
                    text = "Dimas Arturo López Montalvo",
                    style = MaterialTheme.typography.body1.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            coroutineScope.launch {
                                scaffoldState.drawerState.close()
                                navController.navigate("perfil")
                            }
                        }
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
                    text = "¡BIENVENIDO $username!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Muestra la hora actualizada
                Text(
                    text = currentTime,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botones de Entrada y Salida
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    EntryExitButton(
                        text = "Entrada",
                        backgroundColor = Color(0xFF4CAF50),
                        onClick = { /* Acción Entrada */ }
                    )
                    EntryExitButton(
                        text = "Salida",
                        backgroundColor = Color(0xFFF44336),
                        onClick = { /* Acción Salida */ }
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    )
}

@Composable
fun EntryExitButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
    ) {
        Text(text)
    }
}
