package com.example.checkwork.Home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.checkwork.FunctionTime.getCurrentTime
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var hasCheckedIn by remember { mutableStateOf(false) }
    var hasCheckedOut by remember { mutableStateOf(false) }
    var nombreEmpresa by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var companyCode by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    profileImageUrl = document.getString("profileImageUrl")
                    departamento = document.getString("departamento") ?: ""
                    username = document.getString("username") ?: ""
                    isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false
                    userRole = document.getString("rol") ?: ""
                    companyCode = document.getString("company_code") ?: ""

                    // Obtener el nombre de la empresa usando el companyCode
                    if (companyCode.isNotEmpty()) {
                        db.collection("Company_Code").document(companyCode).get()
                            .addOnSuccessListener { companyDoc ->
                                nombreEmpresa = companyDoc.getString("nombreEmpresa") ?: ""
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al obtener el nombre de la empresa: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { Log.e("Firestore", "Error al obtener los datos del usuario") }
        }

        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }


    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    systemUiController.setSystemBarsColor(
        color = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (profileImageUrl != null) {
                            Image(
                                painter = rememberImagePainter(profileImageUrl),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                                    .clickable { navController.navigate("perfil") }
                            )
                        } else {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Imagen predeterminada",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                            )
                        }
                        Switch(
                            checked = isDarkModeEnabled,
                            onCheckedChange = { isChecked ->
                                isDarkModeEnabled = isChecked
                                updateDarkModePreferenceInFirebase(isChecked)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                        )
                    }
                }
            )
        },
        drawerContent = {
            DrawerContent(navController, username, departamento, isDarkModeEnabled, userRole) {
                isDarkModeEnabled = it
                updateDarkModePreferenceInFirebase(it)
            }
        },
        content = {
            ContentSection(username, currentTime, nombreEmpresa, context, hasCheckedIn, hasCheckedOut, isDarkModeEnabled) {
                hasCheckedIn = it.first
                hasCheckedOut = it.second
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

fun showAlert(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}