package com.example.checkwork

import CalendarView
import RegisterScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.checkwork.Home.PantallaPrincipal
import com.example.checkwork.Login.LoginScreen.LoginScreen
import com.example.checkwork.NavigationRegister.CheckHistoryScreen
import com.example.checkwork.Profile.AddNum
import com.example.checkwork.Profile.ProfileScreen
import com.example.checkwork.forgot.ForgotPasswordMethodScreen
import com.example.checkwork.forgot.ForgotPasswordNewPasswordScreen
import com.example.checkwork.forgot.ForgotPasswordScreen
import com.example.checkwork.forgot.ForgotPasswordUpdatedScreen
import com.example.checkwork.form.FormularioEmpresaScreen
import com.example.checkwork.supportview.SoporteScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable(route = "login") { LoginScreen(navController)
                    }
                    composable(route = "register") { RegisterScreen(navController) }
                    composable(route = "home") {
                        PantallaPrincipal(navController)
                        DisableSystemBackButton()
                    }
                    composable(route = "form") { FormularioEmpresaScreen(navController) }
                    composable(route = "perfil") { ProfileScreen(navController) }
                    composable(route = "AddPhone") { AddNum(navController) }
                    composable(route = "support") { SoporteScreen(navController) }
                    composable(route = "calendar") { CalendarView(navController) }
                    composable(route = "navigate_register") { CheckHistoryScreen(navController) }

                    // Navegación para el flujo de recuperación de contraseña
                    composable(route = "forgot_password") { ForgotPasswordScreen(navController) }
                    composable(route = "forgot_password_2") { ForgotPasswordMethodScreen(navController) }
                    composable(route = "forgot_password_3") { ForgotPasswordNewPasswordScreen(navController) }
                    composable(route = "forgot_password_4") { ForgotPasswordUpdatedScreen(navController) }
                }
        }
    }
}

@Composable
fun DisableSystemBackButton() {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val context = LocalContext.current
    val currentOnBackPressed by rememberUpdatedState(newValue = {
        Toast.makeText(context, "Use el botón de cerrar sesión para salir", Toast.LENGTH_SHORT).show()
    })

    DisposableEffect(backPressedDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
        backPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }
}
