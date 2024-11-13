package com.example.checkwork.JoinEmpresa

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun JoinEmpresaScreen(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var companyCode by remember { mutableStateOf("") }
    var showCard by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Recuperar estado del modo oscuro de Firebase
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al obtener el modo oscuro", Toast.LENGTH_SHORT).show()
                }
        }
        delay(1000) // Retraso para la animación
        showCard = true
    }

    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    fun joinCompany() {
        db.collection("Company_Code").document(companyCode).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Código encontrado, asociar al usuario
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("users").document(userId)
                            .update("company_code", companyCode, "rol", "empleado")
                            .addOnSuccessListener {
                                showSuccessDialog = true
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al unirse a la empresa", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Código no encontrado
                    showErrorDialog = true
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al verificar el código de empresa", Toast.LENGTH_SHORT).show()
            }
    }

    systemUiController.setSystemBarsColor(
        color = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unirme a Empresa", color = Color.White) },
                backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            updateDarkModePreferenceInFirebase(it) // Guardar en Firebase
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                    )
                }
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFE0F7FA)),
                contentAlignment = Alignment.Center
            ) {
                AnimatedVisibility(
                    visible = showCard,
                    enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color.White,
                        elevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ingrese el código de la empresa",
                                fontSize = 18.sp,
                                color = if (isDarkModeEnabled) Color.White else Color.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = companyCode,
                                onValueChange = { companyCode = it },
                                label = { Text("Código de Empresa", color = if (isDarkModeEnabled) Color.White else Color.Black) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    textColor = if (isDarkModeEnabled) Color.White else Color.Black,
                                    focusedBorderColor = if (isDarkModeEnabled) Color.White else Color.Black,
                                    unfocusedBorderColor = if (isDarkModeEnabled) Color.Gray else Color.LightGray
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { joinCompany() },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = if (isDarkModeEnabled) Color(0xFF0056E0) else Color(0xFF303030),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    Icons.Filled.GroupAdd,
                                    contentDescription = "Unirme a Empresa",
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Unirme a Empresa")
                            }
                        }
                    }
                }

                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showSuccessDialog = false },
                        title = { Text("Éxito") },
                        text = { Text("Te has unido a la empresa exitosamente.") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showSuccessDialog = false
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            ) {
                                Text("Aceptar")
                            }
                        }
                    )
                }

                if (showErrorDialog) {
                    AlertDialog(
                        onDismissRequest = { showErrorDialog = false },
                        title = { Text("Error") },
                        text = { Text("Código de empresa no encontrado.") },
                        confirmButton = {
                            Button(
                                onClick = { showErrorDialog = false }
                            ) {
                                Text("Aceptar")
                            }
                        }
                    )
                }
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
