package com.example.checkwork.NavigationBar

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun BottomNavigationBar() {
    BottomNavigation(
        backgroundColor = Color(0xFF2196F3)

    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.CalendarToday, contentDescription = "Calendario") },
            label = { Text("Calendario") },
            selected = false,
            onClick = { /* Acción de Calendario */ }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true,
            onClick = { /* Acción Home */ }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Registros") },
            label = { Text("Registros") },
            selected = false,
            onClick = { /* Acción de Registros */ }
        )
    }
}