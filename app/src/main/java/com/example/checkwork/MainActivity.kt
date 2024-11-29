package com.example.checkwork

import RegisterScreen
import ResetPasswordScreen
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import com.example.checkwork.calendar.CalendarView
import com.example.checkwork.form.FormularioEmpresaScreen
import com.example.checkwork.supportview.SoporteScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Registrar permisos de ubicación
        val requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (!fineLocationGranted && !coarseLocationGranted) {
                Toast.makeText(this, "Los permisos de ubicación son necesarios para continuar", Toast.LENGTH_SHORT).show()
            }
        }

        // Verificar permisos al iniciar
        checkLocationPermissions(requestPermissionsLauncher)

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
                composable(route = "reset_password") {
                    ResetPasswordScreen(navController)
                }
                composable(route = "crud") {
                    AdminCrudScreen(navController)
                }
                composable(route = "join_company") {
                    JoinEmpresaScreen(navController)
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

    // Verificar permisos de ubicación
    private fun checkLocationPermissions(requestPermissionsLauncher: ActivityResultLauncher<Array<String>>) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED ||
            coarseLocationPermission != PackageManager.PERMISSION_GRANTED
        ) {
            // Solicitar permisos si no están concedidos
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
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
