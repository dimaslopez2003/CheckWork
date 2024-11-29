package com.example.checkwork.NavigationRegister

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.checkwork.NavigationRegister.dataentryes.CheckEntry

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "MissingPermission")
@Composable
fun CheckHistoryScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val firebaseAnalytics = FirebaseAnalytics.getInstance(LocalContext.current)

    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var userId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var checkEntries by remember { mutableStateOf(listOf<CheckEntry>()) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        userId = auth.currentUser?.uid.orEmpty()
        if (userId.isNotEmpty()) {
            // Registrar el evento al iniciar la consulta
            firebaseAnalytics.logEvent("fetch_check_entries") {
                param("user_id", userId)
            }

            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                profileImageUrl = document.getString("profileImageUrl")
                username = document.getString("username") ?: "Usuario"
                departamento = document.getString("departamento") ?: "Sin Departamento"
                isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false

                db.collection("checks")
                    .whereEqualTo("employeeId", userId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val entries = querySnapshot.documents.mapNotNull { doc ->
                            val fecha = doc.getString("fecha") ?: "Sin fecha"
                            val hora = doc.getString("hora") ?: "Sin hora"
                            val tipo = doc.getString("tipo") ?: "Sin tipo"
                            val latitud = doc.getDouble("latitud")?.toString() ?: "Sin latitud" // Convertir a String
                            val longitud = doc.getDouble("longitud")?.toString() ?: "Sin longitud"
                            CheckEntry(fecha, hora, tipo, latitud, longitud)
                        }
                        checkEntries = entries.sortedByDescending { it.fecha + it.hora }

                        // Registrar cuántos registros se obtuvieron
                        firebaseAnalytics.logEvent("check_entries_fetched") {
                            param("entry_count", entries.size.toLong())
                            param("user_id", userId)
                        }
                    }
            }
        }
    }

    // Guardar estado del modo oscuro en Firebase
    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        if (userId.isNotEmpty()) {
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Entradas y Salidas", color = Color.White) },
                backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = {
                        if (isBackButtonEnabled) {
                            isBackButtonEnabled = false
                            navController.popBackStack()
                        }
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            updateDarkModePreferenceInFirebase(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                    )
                }
            )
        },
        content = {
            val companyCode = "COMP55538" // Define o recupera el código de la empresa desde Firestore

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFE0F7FA))
                    .padding(16.dp)
            ) {
                ProfileCard(profileImageUrl, username, departamento, isDarkModeEnabled)
                Spacer(modifier = Modifier.height(16.dp))
                // Pasa los parámetros requeridos a RecordsCard
                RecordsCard(
                    checkEntries = checkEntries,
                    isDarkModeEnabled = isDarkModeEnabled,
                    employeeId = userId,
                    departamento = departamento
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                isDarkModeEnabled = isDarkModeEnabled
            )
        }
    )
}

