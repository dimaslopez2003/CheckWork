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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

data class BottomNavItem(
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    isDarkModeEnabled: Boolean
) {
    BottomNavigation(
        backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0),
        contentColor = if (isDarkModeEnabled) Color.White else Color.White
    ) {
        val items = listOf(
            BottomNavItem(Icons.Default.CalendarToday, "Calendario"),
            BottomNavItem(Icons.Default.Home, "Home"),
            BottomNavItem(Icons.Default.List, "Registros")
        )

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label, tint = Color.White) },
                label = { Text(item.label, color = Color.White) },
                selected = false,  // Aquí puedes implementar la lógica para manejar el ítem seleccionado
                onClick = {
                    when (item.label) {
                        "Calendario" -> navController.navigate("calendar")
                        "Home" -> navController.navigate("home")
                        "Registros" -> navController.navigate("navigate_register")
                    }
                }
            )
        }
    }
}
