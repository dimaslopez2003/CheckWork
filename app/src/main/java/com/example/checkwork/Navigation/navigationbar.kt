package com.example.checkwork.Navigation

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
import androidx.navigation.NavHostController

data class BottomNavItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
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
                selected = false,  // Puedes implementar la lógica para el ítem seleccionado
                onClick = {
                    when (item.label) {
                        "Calendario" -> navController.navigate("calendario")
                        "Home" -> navController.navigate("home")
                        "Registros" -> navController.navigate("registros")
                    }
                }
            )
        }
    }
}
