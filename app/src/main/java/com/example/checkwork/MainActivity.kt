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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.checkwork.CRUD_USERS.ViewEmployeeRecordsScreen
import com.example.checkwork.Home.PantallaPrincipal
import com.example.checkwork.JoinEmpresa.JoinEmpresaScreen
import com.example.checkwork.Login.LoginScreen
import com.example.checkwork.NavigationRegister.CheckHistoryScreen
import com.example.checkwork.Profile.AddNum
import com.example.checkwork.Profile.ProfileScreen
import com.example.checkwork.CRUD_USERS.FunctionsCrud.AdminCrudScreen
import com.example.checkwork.forgot.*
import com.example.checkwork.form.FormularioEmpresaScreen
import com.example.checkwork.supportview.SoporteScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        setContent {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val navController = rememberNavController()

            var isAdmin by remember { mutableStateOf(false) }
            val userId = auth.currentUser?.uid

            LaunchedEffect(userId) {
                if (userId != null) {
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            isAdmin = document.getString("rol") == "Administrador"
                        }
                }
            }

            NavHost(navController = navController, startDestination = "login") {
                composable(route = "login") {
                    LoginScreen(navController = navController)
                }
                composable(route = "register") {
                    RegisterScreen(navController)
                }
                composable(route = "home") {
                    PantallaPrincipal(navController)
                    DisableSystemBackButton()
                }
                composable(route = "form") {
                    FormularioEmpresaScreen(navController)
                }
                composable(route = "perfil") {
                    ProfileScreen(navController)
                }
                composable(route = "AddPhone") {
                    AddNum(navController)
                }
                composable("support") {
                    SoporteScreen(navController)
                }

                composable(route = "calendar") {
                    CalendarView(navController)
                }
                composable(route = "navigate_register") {
                    CheckHistoryScreen(navController)
                }
                composable(route = "forgot_password") {
                    ForgotPasswordScreen(navController)
                }
                composable(route = "forgot_password_2") {
                    ForgotPasswordMethodScreen(navController)
                }
                composable(route = "forgot_password_3") {
                    ForgotPasswordNewPasswordScreen(navController)
                }
                composable(route = "forgot_password_4") {
                    ForgotPasswordUpdatedScreen(navController)
                }
                composable(route = "crud") {
                    AdminCrudScreen(navController)
                }
                composable(route = "join_company") { JoinEmpresaScreen(navController)
                }
                composable(route = "viewGetRegisterScreen/{employeeId}") { backStackEntry ->
                    val employeeId = backStackEntry.arguments?.getString("employeeId")
                    if (employeeId != null) {
                        ViewEmployeeRecordsScreen(navController, employeeId)
                    }
                }
            }
        }
    }
}

@Composable
fun DisableSystemBackButton() {
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val context = LocalContext.current
    val currentOnBackPressed by rememberUpdatedState(newValue = {
        Toast.makeText(context, "Use el botón de cerrar sesión para salir", Toast.LENGTH_SHORT)
            .show()
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
