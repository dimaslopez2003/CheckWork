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

    // Obtener datos del usuario y los registros de entrada/salida
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                profileImageUrl = document.getString("profileImageUrl")
                username = document.getString("username") ?: ""
                employeeId = document.getString("employeeId") ?: ""
                departamento = document.getString("departamento") ?: ""

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Entradas y Salidas", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA))
                    .padding(16.dp)
            ) {
                // Card del perfil
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp
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
                                tint = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = departamento,
                                fontSize = 16.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = username,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color.White,
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
                            TableHeader("Fecha")
                            TableHeader("Hora")
                            TableHeader("Tipo")
                        }
                        Divider(color = Color.Gray)

                        checkEntries.forEach { entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TableCell(entry.fecha)
                                TableCell(entry.hora)
                                TableCell(
                                    text = entry.tipo.replaceFirstChar { it.uppercase() },
                                    backgroundColor = if (entry.tipo.lowercase() == "entrada") Color(0xFF4CAF50) else Color(0xFFF44336),
                                    textColor = Color.White
                                )
                            }
                            Divider(color = Color.LightGray)
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    )
}

@Composable
fun TableHeader(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        fontSize = 16.sp
    )
}

@Composable
fun TableCell(text: String, backgroundColor: Color = Color.Transparent, textColor: Color = Color.Black) {
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
