package com.example.checkwork.admin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    LaunchedEffect(companyCode) {
        if (companyCode.isNotEmpty()) {
            loadEmployees(db, companyCode, empleados)
        }
    }

    LaunchedEffect(Unit) {
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
                                db = db,
                                onUpdate = { loadEmployees(db, companyCode, empleados) } // Refresca al editar o eliminar
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
fun EmployeeRow(empleado: Map<String, Any>, isDarkModeEnabled: Boolean, db: FirebaseFirestore, onUpdate: () -> Unit) {
    var showEditDialog by remember { mutableStateOf(false) }
    if (showEditDialog) {
        EditEmployeeDialog(
            empleado = empleado,
            onDismiss = {
                showEditDialog = false
                onUpdate() // Refresca después de editar
            },
            db = db
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("ID: ${empleado["employeeId"]}", color = if (isDarkModeEnabled) Color.White else Color.Black)
            Text("Nombre: ${empleado["username"]}", color = if (isDarkModeEnabled) Color.White else Color.Black)
            Text("Departamento: ${empleado["departamento"] ?: "N/A"}", color = if (isDarkModeEnabled) Color.White else Color.Black)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { showEditDialog = true }) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = if (isDarkModeEnabled) Color.White else Color.Black)
            }
            IconButton(onClick = {
                removeCompanyCodeFromEmployee(db, empleado["documentId"].toString(), onUpdate)
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = if (isDarkModeEnabled) Color.White else Color.Black)
            }
        }
    }
}

@Composable
fun EditEmployeeDialog(empleado: Map<String, Any>, onDismiss: () -> Unit, db: FirebaseFirestore) {
    var newUsername by remember { mutableStateOf(empleado["username"].toString()) }
    var newEmployeeId by remember { mutableStateOf(empleado["employeeId"].toString()) }
    var newDepartamento by remember { mutableStateOf(empleado["departamento"]?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Empleado") },
        text = {
            Column {
                OutlinedTextField(
                    value = newUsername,
                    onValueChange = { newUsername = it },
                    label = { Text("Nombre") }
                )
                OutlinedTextField(
                    value = newEmployeeId,
                    onValueChange = { newEmployeeId = it },
                    label = { Text("ID de Empleado") }
                )
                OutlinedTextField(
                    value = newDepartamento,
                    onValueChange = { newDepartamento = it },
                    label = { Text("Departamento") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    updateEmployeeInFirebase(db, empleado["documentId"].toString(), newUsername, newEmployeeId, newDepartamento)
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun updateEmployeeInFirebase(db: FirebaseFirestore, documentId: String, username: String, employeeId: String, departamento: String) {
    val updatedData = mapOf(
        "username" to username,
        "employeeId" to employeeId,
        "departamento" to departamento
    )
    db.collection("users").document(documentId).update(updatedData)
        .addOnSuccessListener {
            Log.d("Firestore", "Empleado actualizado exitosamente")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al actualizar el empleado", e)
        }
}

fun removeCompanyCodeFromEmployee(db: FirebaseFirestore, documentId: String, onUpdate: () -> Unit) {
    db.collection("users").document(documentId).update("company_code", null)
        .addOnSuccessListener {
            Log.d("Firestore", "Código de la empresa eliminado del empleado")
            onUpdate() // Refresca la lista después de eliminar el company_code
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al eliminar el código de la empresa", e)
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
                val employeeData = document.data.toMutableMap()
                employeeData["documentId"] = document.id
                empleados.add(employeeData)
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
