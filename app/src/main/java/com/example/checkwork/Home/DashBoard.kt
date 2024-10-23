package com.example.checkwork.Home

import HamburgerMenu
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal(navController: NavHostController, username: String?) {

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFF0056E0))


    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }

    // Estado para el Scaffold
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
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
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Menú", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = {
                    scope.launch { scaffoldState.drawerState.close() }
                    navController.navigate("home")
                }) {
                    Text("Home")
                }

                TextButton(onClick = {
                    scope.launch { scaffoldState.drawerState.close() }
                    navController.navigate("form")
                }) {
                    Text("Formulario")
                }

                TextButton(onClick = {
                    scope.launch { scaffoldState.drawerState.close() }
                    navController.navigate("login")
                }) {
                    Text("Cerrar Sesión")
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
                    text = "¡BIENVENIDO!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentTime,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    EntryExitButton(
                        text = "Entrada",
                        backgroundColor = Color(0xFF4CAF50),
                        onClick = { /* Acción Entrada */ })
                    EntryExitButton(
                        text = "Salida",
                        backgroundColor = Color(0xFFF44336),
                        onClick = { /* Acción Salida */ })
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
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


data class BottomNavItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)
