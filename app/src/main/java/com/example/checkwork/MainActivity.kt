package com.example.checkwork

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkwork.ui.theme.CheckWorkTheme
import kotlinx.coroutines.delay
import com.example.checkwork.FunctionTime.getCurrentTime
import com.example.checkwork.NavigationBar.BottomNavigationBar
import com.example.checkwork.dashboard.RelojChecadorApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckWorkTheme {
                RelojChecadorApp()
                getCurrentTime()
            }
        }
    }
}

@Composable
fun PantallaPrincipal() {
    // Estado para el tiempo
    var currentTime by remember { mutableStateOf(getCurrentTime()) }

    // Efecto que actualiza el tiempo cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)  // Espera 1 segundo
            currentTime = getCurrentTime()  // Actualiza la hora
        }
    }

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

        // Muestra la hora actualizada en tiempo real
        Text(
            text = currentTime,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Button(
                onClick = { /* Registrar entrada */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF4CAF50))
            ) {
                Text("Entrada")
            }
            Button(
                onClick = { /* Registrar salida */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF44336))
            ) {
                Text("Salida")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CheckWorkTheme {
        RelojChecadorApp()
    }
}
