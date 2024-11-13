package com.example.checkwork.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminCrudScreen(navController = rememberNavController())
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCrudScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var companyCode by remember { mutableStateOf("") }
    val empleados = remember { mutableStateListOf<Map<String, Any>>() }

    // Observa cambios en companyCode y recarga los empleados
    LaunchedEffect(companyCode) {
        if (companyCode.isNotEmpty()) {
            loadEmployees(db, companyCode, empleados)
        }
    }

    LaunchedEffect(Unit) {
        // Obtener el código de la empresa del administrador
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false
                    companyCode = document.getString("company_code") ?: ""
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al obtener los datos del administrador", e)
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Admin Panel", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
                ),
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
                            updateDarkModePreferenceInFirebase(auth, db, it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                    )
                }
            )
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFFFFFFF)),
                color = if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFFFFFFF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Empleados en la empresa", style = MaterialTheme.typography.headlineMedium, color = if (isDarkModeEnabled) Color.White else Color.Black)

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(empleados.size) { index ->
                            EmployeeRow(
                                empleado = empleados[index],
                                isDarkModeEnabled = isDarkModeEnabled,
                                navController = navController
                            )
                            Divider(color = if (isDarkModeEnabled) Color.Gray else Color.LightGray, thickness = 1.dp)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun EmployeeRow(empleado: Map<String, Any>, isDarkModeEnabled: Boolean, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Información del empleado
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("ID: ${empleado["employeeId"]}", color = if (isDarkModeEnabled) Color.White else Color.Black)
            Text("Nombre: ${empleado["username"]}", color = if (isDarkModeEnabled) Color.White else Color.Black)
            Text("Departamento: ${empleado["departamento"] ?: "N/A"}", color = if (isDarkModeEnabled) Color.White else Color.Black)
        }
        // Íconos de acción
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { /* Editar Acción */ }) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = if (isDarkModeEnabled) Color.White else Color.Black)
            }
            IconButton(onClick = { /* Eliminar Acción */ }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = if (isDarkModeEnabled) Color.White else Color.Black)
            }
            //Boton que llevará a la vista de los registros de cada empleado
            //IconButton(onClick = { navController.navigate("view_registers") }) {
                Icon(Icons.Filled.Visibility, contentDescription = "Ver Registros", tint = if (isDarkModeEnabled) Color.White else Color.Black)
            }
        }
    }

fun loadEmployees(db: FirebaseFirestore, companyCode: String, empleados: MutableList<Map<String, Any>>) {
    db.collection("users")
        .whereEqualTo("company_code", companyCode)
        .whereEqualTo("rol", "empleado")
        .get()
        .addOnSuccessListener { documents ->
            empleados.clear()
            for (document in documents) {
                empleados.add(document.data)
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al cargar los empleados", e)
        }
}

fun updateDarkModePreferenceInFirebase(auth: FirebaseAuth, db: FirebaseFirestore, isDarkMode: Boolean) {
    auth.currentUser?.uid?.let { userId ->
        db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
    }
}
