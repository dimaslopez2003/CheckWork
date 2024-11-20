package com.example.checkwork.NavigationRegister

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

data class CheckEntry(
    val fecha: String,
    val hora: String,
    val tipo: String,
    val comentarios: String = ""
)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CheckHistoryScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var employeeId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var checkEntries by remember { mutableStateOf(listOf<CheckEntry>()) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    // Recuperar el estado de modo oscuro de Firebase
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                profileImageUrl = document.getString("profileImageUrl")
                username = document.getString("username") ?: ""
                employeeId = document.getString("employeeId") ?: ""
                departamento = document.getString("departamento") ?: ""
                isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false  // Obtener el estado del modo oscuro

                if (employeeId.isNotEmpty()) {
                    db.collection("checks").whereEqualTo("employeeId", employeeId).get()
                        .addOnSuccessListener { querySnapshot ->
                            val entries = querySnapshot.documents.mapNotNull { doc ->
                                val fecha = doc.getString("fecha") ?: ""
                                val hora = doc.getString("hora") ?: ""
                                val tipo = doc.getString("tipo") ?: ""
                                CheckEntry(fecha = fecha, hora = hora, tipo = tipo)
                            }
                            checkEntries = entries
                        }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        delay(500)
        isBackButtonEnabled = true
    }

    // Guardar estado del modo oscuro en Firebase
    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
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
                    }){
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Interruptor de modo oscuro en la barra superior
                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            updateDarkModePreferenceInFirebase(it) // Guardar el estado en Firebase
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
                // Card del perfil
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (profileImageUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(profileImageUrl),
                                contentDescription = "Foto de Perfil",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = "Imagen de Perfil",
                                modifier = Modifier.size(80.dp),
                                tint = if (isDarkModeEnabled) Color.Gray else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = departamento,
                                fontSize = 16.sp,
                                color = if (isDarkModeEnabled) Color.LightGray else Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = username,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkModeEnabled) Color.White else Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card para la lista de registros
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color.White,
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        // Encabezado de la tabla
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TableHeader("Fecha", isDarkModeEnabled)
                            TableHeader("Hora", isDarkModeEnabled)
                            TableHeader("Tipo", isDarkModeEnabled)
                        }
                        Divider(color = if (isDarkModeEnabled) Color.Gray else Color.LightGray)

                        checkEntries.forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TableCell(entry.fecha, isDarkModeEnabled)
                                TableCell(entry.hora, isDarkModeEnabled)
                                TableCell(entry.tipo, isDarkModeEnabled)
                            }
                            Divider(color = if (isDarkModeEnabled) Color.Gray else Color.LightGray)
                        }
                    }
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
fun TableCell(text: String, isDarkModeEnabled: Boolean, backgroundColor: Color = Color.Transparent, textColor: Color = if (isDarkModeEnabled) Color.LightGray else Color.Black) {
    Box(
        modifier = Modifier
            .background(backgroundColor)
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