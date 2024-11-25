package com.example.checkwork.CRUD_USERS

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEmployeeRecordsScreen(
    navController: NavHostController,
    employeeId: String,
    viewModel: EmployeeViewModel = viewModel(factory = EmployeeViewModelFactory(EmployeeRepository()))
) {
    val employeeRecords by viewModel.employeeRecords.collectAsState()
    val employeeName by viewModel.employeeName.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var isBackButtonEnabled by remember { mutableStateOf(false) } // Inicia en false para aplicar el delay
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Iniciar la carga de datos usando fetchCheckEntries
    LaunchedEffect(employeeId) {
        try {
            isLoading = true
            println("Cargando registros para employeeId: $employeeId")
            viewModel.fetchEmployeeRecords(employeeId) // Se asegura de cargar registros con la lógica correcta
            viewModel.fetchEmployeeName(employeeId)
        } catch (e: Exception) {
            isError = true
            println("Error al cargar datos en la vista: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Delay para habilitar el botón de retroceso
    LaunchedEffect(Unit) {
        delay(500)
        isBackButtonEnabled = true
    }

    // Actualizar preferencia de modo oscuro en Firebase
    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de Empleado") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isBackButtonEnabled) {
                            isBackButtonEnabled = false // Deshabilitar para evitar múltiples pulsaciones
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    // Mostrar indicador de carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                isError -> {
                    // Mostrar mensaje de error
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error al cargar los datos.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                employeeRecords.isEmpty() -> {
                    // Mostrar mensaje si no hay registros
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay registros disponibles.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                else -> {
                    // Mostrar lista de registros
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Registros de: $employeeName",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(employeeRecords) { record ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(record.fecha, modifier = Modifier.weight(1f))
                                    Text(record.hora, modifier = Modifier.weight(1f))
                                    Text(record.tipo, modifier = Modifier.weight(1f))
                                }
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}

class EmployeeViewModelFactory(
    private val repository: EmployeeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmployeeViewModel::class.java)) {
            return EmployeeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
