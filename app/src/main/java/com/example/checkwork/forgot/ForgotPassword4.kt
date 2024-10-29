// ForgotPassword4.kt
package com.example.checkwork.forgot
// ForgotPasswordUpdatedScreen.kt
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ForgotPasswordUpdatedScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Regresar", tint = Color.White)
                    }
                },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("PASSWORD UPDATED", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(80.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Text("Your password has been updated!", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFF0056E0))
                ) {
                    Text("LOGIN", color = Color.White)
                }
            }
        }
    )
}
