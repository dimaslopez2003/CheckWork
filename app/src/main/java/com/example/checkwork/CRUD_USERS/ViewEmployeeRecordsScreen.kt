package com.example.checkwork.CRUD_USERS

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.checkwork.R
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
    var isBackButtonEnabled by remember { mutableStateOf(false) }
    val checkComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.check)) // Animación para la Card
    val noRecordsComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.notfound)) // Animación sin registros
    val checkAnimationState = animateLottieCompositionAsState(checkComposition, iterations = 1) // Una sola iteración

    // Cargar datos iniciales
    LaunchedEffect(employeeId) {
        try {
            isLoading = true
            viewModel.fetchEmployeeRecords(employeeId)
            viewModel.fetchEmployeeName(employeeId)
        } catch (e: Exception) {
            isError = true
            println("Error al cargar datos: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // Delay para habilitar el botón de retroceso
    LaunchedEffect(Unit) {
        delay(500)
        isBackButtonEnabled = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de Empleado", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0056E0)),
                navigationIcon = {
                    IconButton(onClick = {
                        if (isBackButtonEnabled) {
                            isBackButtonEnabled = false
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
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
            Column(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (isError) {
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
                } else if (employeeRecords.isEmpty()) {
                    // Mostrar animación y texto cuando no hay registros
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LottieAnimation(
                            composition = noRecordsComposition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay registros disponibles.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Card con texto más pequeño y animación
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
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
                                        text = "Registros de: $employeeName",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 16.sp,
                                            color = Color(0xFF1E88E5)
                                        )
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
