package com.example.checkwork

import RegisterScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.checkwork.Home.PantallaPrincipal
import com.example.checkwork.Login.LoginScreen.LoginScreen
import com.example.checkwork.form.FormularioEmpresaScreen
import com.example.checkwork.ui.theme.CheckWorkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckWorkTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable(route = "login") { LoginScreen(navController) }
                    composable(route = "register") { RegisterScreen(navController) }
                    composable(route = "home") { PantallaPrincipal() }
                    composable(route = "form") { FormularioEmpresaScreen(navController) }
                }
            }
        }
    }
}
