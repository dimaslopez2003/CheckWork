package com.example.checkwork.Home

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
import com.example.checkwork.FunctionTime.getCurrentTime
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal() {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFF0056E0))

    // Estado para el tiempo
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    // Actualiza el tiempo cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { /* Acción Menú */ }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción Modo Oscuro */ }) {
                        Icon(Icons.Filled.Brightness2, contentDescription = "DarkMode", tint = Color.White)
                    }
                }
            )
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
                    EntryExitButton(text = "Entrada", backgroundColor = Color(0xFF4CAF50), onClick = { /* Acción Entrada */ })
                    EntryExitButton(text = "Salida", backgroundColor = Color(0xFFF44336), onClick = { /* Acción Salida */ })
                }
            }
        },
        bottomBar = {
            BottomNavigationBar()
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

@Composable
fun BottomNavigationBar() {
    BottomNavigation(backgroundColor = Color(0xFF0056E0)) {
        val items = listOf(
            BottomNavItem(Icons.Default.CalendarToday, "Calendario"),
            BottomNavItem(Icons.Default.Home, "Home"),
            BottomNavItem(Icons.Default.List, "Registros")
        )

        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label, tint = Color.White) },
                label = { Text(item.label, color = Color.White) },
                selected = index == 1, // Establece el elemento seleccionado
                onClick = { /* Acción de navegación */ }
            )
        }
    }
}

data class BottomNavItem(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)
