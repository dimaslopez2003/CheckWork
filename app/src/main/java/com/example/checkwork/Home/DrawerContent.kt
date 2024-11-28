package com.example.checkwork.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Help
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.checkwork.R

@Composable
fun DrawerContent(
    navController: NavHostController,
    username: String,
    departamento: String,
    isDarkModeEnabled: Boolean,
    userRole: String,
    onDarkModeToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF042159))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Text(
            text = departamento,
            style = MaterialTheme.typography.h6.copy(color = if (isDarkModeEnabled) Color.LightGray else Color.White),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = username,
            style = MaterialTheme.typography.body1.copy(color = if (isDarkModeEnabled) Color.LightGray else Color.White),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("perfil") }
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Perfil",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Perfil", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
                Text(
                    text = "Agrega una foto para identificarte.",
                    color = if (isDarkModeEnabled) Color.Gray else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = if (isDarkModeEnabled) Color.Gray else Color.White.copy(alpha = 0.3f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Brightness2,
                contentDescription = "Modo Oscuro",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Modo Oscuro", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
                Text(
                    text = "Para descansar tu vista activa modo oscuro.",
                    color = if (isDarkModeEnabled) Color.Gray else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = isDarkModeEnabled,
                onCheckedChange = { onDarkModeToggle(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    if (userRole == "Administrador") {
                        navController.navigate("form")
                    } else {
                        navController.navigate("join_company")
                    }
                }
        ) {
            Icon(
                imageVector = if (userRole == "Administrador") Icons.Filled.Edit else Icons.Filled.GroupAdd,
                contentDescription = if (userRole == "Administrador") "Registrar Empresa" else "Unirme a Empresa",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (userRole == "Administrador") "Registrar Empresa" else "Unirme a Empresa",
                color = if (isDarkModeEnabled) Color.LightGray else Color.White,
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("support") }
        ) {
            Icon(
                imageVector = Icons.Filled.Help,
                contentDescription = "Soporte y Asistencia",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Soporte y Asistencia", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("login") }
        ) {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Cerrar Sesión",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Cerrar Sesión", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
        }
    }
}