package com.example.checkwork.form

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.checkwork.data.model.AuthManager

class FormularioEmpresaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FormularioEmpresaScreen(navController = rememberNavController())
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioEmpresaScreen(navController: NavHostController) {
    var nombreEmpresa by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var cp by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var rfc by remember { mutableStateOf("") }
    var cpError by remember { mutableStateOf(false) }
    var rfcError by remember { mutableStateOf(false) }

    val db = Firebase.firestore
    val authManager = AuthManager()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Registra tu empresa", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0056E0)
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF66ABE3)),
                color = Color(0xFF66ABE3)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE0F7FA),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        elevation = CardDefaults.cardElevation(8.dp)
                    )
                    {
                        Column(
                            modifier = Modifier
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalance,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(8.dp),
                                tint = Color(0xFF000000)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Campo Nombre de la Empresa
                            OutlinedTextField(
                                value = nombreEmpresa,
                                onValueChange = { nombreEmpresa = it },
                                label = { Text("Nombre de la Empresa") },
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Fila para Código Postal y RFC
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = cp,
                                    onValueChange = {
                                        if (it.length <= 5 && it.all { char -> char.isDigit() }) {
                                            cp = it
                                            cpError = it.length != 5
                                        }
                                    },
                                    label = { Text("Código Postal") },
                                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                    modifier = Modifier.weight(1f),
                                    isError = cpError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                OutlinedTextField(
                                    value = rfc,
                                    onValueChange = {
                                        if (it.length <= 13) {
                                            rfc = it
                                            rfcError = it.length != 13
                                        }
                                    },
                                    label = { Text("RFC") },
                                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                    modifier = Modifier.weight(1f),
                                    isError = rfcError
                                )
                            }

                            if (cpError) {
                                Text(
                                    text = "El código postal debe tener 5 dígitos",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (rfcError) {
                                Text(
                                    text = "El RFC debe tener 13 caracteres",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Fila para Ciudad y Estado
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = ciudad,
                                    onValueChange = { ciudad = it },
                                    label = { Text("Ciudad") },
                                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = estado,
                                    onValueChange = { estado = it },
                                    label = { Text("Estado") },
                                    textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Campo Dirección
                            OutlinedTextField(
                                value = direccion,
                                onValueChange = { direccion = it },
                                label = { Text("Dirección") },
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Campo País
                            OutlinedTextField(
                                value = pais,
                                onValueChange = { pais = it },
                                label = { Text("País") },
                                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val empresaData = hashMapOf(
                                        "nombre_empresa" to nombreEmpresa,
                                        "ciudad" to ciudad,
                                        "cp" to cp,
                                        "direccion" to direccion,
                                        "estado" to estado,
                                        "pais" to pais,
                                        "rfc" to rfc
                                    )
                                    // Lógica para guardar en Firestore
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0056E0),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Registrar Empresa")
                            }
                        }
                    }
                }
            }
        }
    )
}
