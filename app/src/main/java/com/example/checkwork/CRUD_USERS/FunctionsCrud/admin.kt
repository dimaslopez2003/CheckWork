package com.example.checkwork.CRUD_USERS.FunctionsCrud

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SwitchDefaults
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
import com.airbnb.lottie.compose.*
import com.example.checkwork.Navigation.BottomNavigationBar
import com.example.checkwork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AdminCrudScreen(navController)
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
    var isBackButtonEnabled by remember { mutableStateOf(true) }
    val checkComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check))
    val checkAnimationState = animateLottieCompositionAsState(checkComposition, iterations = 1) // Una sola iteración


    // Cargar datos iniciales del administrador y empresa
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

    // Cargar empleados cuando se tenga un código de empresa
    LaunchedEffect(companyCode) {
        if (companyCode.isNotEmpty()) {
            loadEmployees(db, companyCode, empleados)
        }
    }

    LaunchedEffect(Unit) {
        delay(500)
        isBackButtonEnabled = true
    }

    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
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
                    // Interruptor de modo oscuro en la barra superior
                    androidx.compose.material.Switch(
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
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                isDarkModeEnabled = isDarkModeEnabled
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFFFFFFF)),
                color = if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFFFFFFF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
// Código dentro de la función AdminCrudScreen
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkModeEnabled) Color(0xFF424242) else Color(
                                0xFFE3F2FD
                            ) // Gris para modo oscuro
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Empleados en la empresa",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = if (isDarkModeEnabled) Color.LightGray else Color(
                                        0xFF1E88E5
                                    ) // Texto claro en modo oscuro
                                )
                            }
                            LottieAnimation(
                                composition = checkComposition,
                                progress = checkAnimationState.progress,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(empleados) { empleado ->
                            EmployeeRow(
                                empleado = empleado,
                                isDarkModeEnabled = isDarkModeEnabled,
                                db = db,
                                navController = navController,
                                onUpdate = { loadEmployees(db, companyCode, empleados) }
                            )
                            Divider(
                                color = if (isDarkModeEnabled) Color.Gray else Color.LightGray,
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun EmployeeRow(
    empleado: Map<String, Any>,
    isDarkModeEnabled: Boolean,
    db: FirebaseFirestore,
    navController: NavHostController,
    onUpdate: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    if (showEditDialog) {
        EditEmployeeDialog(
            empleado = empleado,
            onDismiss = {
                showEditDialog = false
                onUpdate()
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "ID: ${empleado["employeeId"]}",
                color = if (isDarkModeEnabled) Color.White else Color.Black
            )
            Text(
                text = "Nombre: ${empleado["username"]}",
                color = if (isDarkModeEnabled) Color.White else Color.Black
            )
            Text(
                text = "Departamento: ${empleado["departamento"] ?: "N/A"}",
                color = if (isDarkModeEnabled) Color.White else Color.Black
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                navController.navigate("viewGetRegisterScreen/${empleado["documentId"]}")
            }) {
                Icon(
                    Icons.Filled.Visibility,
                    contentDescription = "Ver registros",
                    tint = if (isDarkModeEnabled) Color.White else Color.Black
                )
            }
            IconButton(onClick = { showEditDialog = true }) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = if (isDarkModeEnabled) Color.White else Color.Black
                )
            }
            IconButton(onClick = {
                removeCompanyCodeFromEmployee(db, empleado["documentId"].toString(), onUpdate)
            }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = if (isDarkModeEnabled) Color.White else Color.Black
                )
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
                    updateEmployeeInFirebase(
                        db,
                        empleado["documentId"].toString(),
                        newUsername,
                        newEmployeeId,
                        newDepartamento
                    )
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