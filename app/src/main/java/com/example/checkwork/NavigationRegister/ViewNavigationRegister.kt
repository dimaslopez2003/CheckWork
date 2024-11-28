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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.checkwork.NavigationRegister.dataentryes.CheckEntry

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CheckHistoryScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val firebaseAnalytics = FirebaseAnalytics.getInstance(LocalContext.current) // Mover aquí

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
                            val comentarios = doc.getString("comentarios") ?: ""
                            CheckEntry(fecha, hora, tipo, comentarios)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFE0F7FA))
                    .padding(16.dp)
            ) {
                ProfileCard(profileImageUrl, username, departamento, isDarkModeEnabled)
                Spacer(modifier = Modifier.height(16.dp))
                RecordsCard(checkEntries, isDarkModeEnabled)
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

@Composable
fun DatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    android.app.DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
        val formattedDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
        onDateSelected(formattedDate) // Devolver la fecha seleccionada
    }, year, month, day).apply {
        setOnDismissListener { onDismissRequest() }
    }.show()
}

@Composable
fun TableHeader(text: String, isDarkModeEnabled: Boolean) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = if (isDarkModeEnabled) Color.LightGray else Color.Black,
        fontSize = 16.sp
    )
}

@Composable
fun TableCell(text: String, isDarkModeEnabled: Boolean, textColor: Color = if (isDarkModeEnabled) Color.LightGray else Color.Black) {
    Box(
        modifier = Modifier
            .padding(horizontal = 1.dp, vertical = 12.dp)
            .fillMaxWidth(0.3f)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp
        )
    }
}