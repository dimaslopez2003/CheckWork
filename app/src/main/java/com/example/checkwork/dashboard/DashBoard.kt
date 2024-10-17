package com.example.checkwork.dashboard

import android.annotation.SuppressLint
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.checkwork.NavigationBar.BottomNavigationBar
import com.example.checkwork.PantallaPrincipal

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RelojChecadorApp() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WorkCheckApp") },
                backgroundColor = Color(0xFF2196F3),
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Acción del icono reloj */ }) {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Reloj")
                    }
                }
            )
        },
        content = {
            PantallaPrincipal()
        },
        bottomBar = {
            BottomNavigationBar()
        }
    )
}