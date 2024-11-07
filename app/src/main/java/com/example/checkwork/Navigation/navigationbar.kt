package com.example.checkwork.Navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackupTable
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    isDarkModeEnabled: Boolean
) {
    var isAdmin by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Verifica si el usuario es administrador
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    isAdmin = document.getString("rol") == "Administrador"
                }
        }
    }

    val items = mutableListOf(
        BottomNavItem(Icons.Default.CalendarToday, "Calendario", "calendar"),
        BottomNavItem(Icons.Default.Home, "Home", "home"),
        BottomNavItem(Icons.Default.List, "Registros", "navigate_register")
    )

    if (isAdmin) {
        items.add(BottomNavItem(Icons.Default.BackupTable, "Admin", "crud"))
    }

    BottomNavigation(
        backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0),
        contentColor = Color.White
    ) {
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = item.label, tint = Color.White) },
                label = { Text(item.label, color = Color.White) },
                selected = false,
                onClick = {
                    when (item.route) {
                        "calendar" -> navController.navigate("calendar")
                        "home" -> navController.navigate("home")
                        "navigate_register" -> navController.navigate("navigate_register")
                        "crud" -> navController.navigate("crud")
                    }
                }
            )
        }
    }
}
