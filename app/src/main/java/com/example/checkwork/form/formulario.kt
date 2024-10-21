package com.example.checkwork.form

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var nombreUsuario by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Empleado") }

    val db = Firebase.firestore
    val authManager = AuthManager()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Empresa") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Campo Nombre de la Empresa
                TextField(
                    value = nombreEmpresa,
                    onValueChange = { nombreEmpresa = it },
                    label = { Text("Nombre de la Empresa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Ciudad
                TextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Código Postal (tipo número pero guardado como string)
                TextField(
                    value = cp,
                    onValueChange = { cp = it },
                    label = { Text("Código Postal") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Dirección
                TextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo Estado
                TextField(
                    value = estado,
                    onValueChange = { estado = it },
                    label = { Text("Estado") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo País
                TextField(
                    value = pais,
                    onValueChange = { pais = it },
                    label = { Text("País") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Campo RFC
                TextField(
                    value = rfc,
                    onValueChange = { rfc = it },
                    label = { Text("RFC") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Nombre de Usuario
                TextField(
                    value = nombreUsuario,
                    onValueChange = { nombreUsuario = it },
                    label = { Text("Nombre de Usuario") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Selector de Rol
                Text("Rol:")
                Row {
                    RadioButton(
                        selected = rol == "Administrador",
                        onClick = { rol = "Administrador" }
                    )
                    Text(text = "Administrador")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = rol == "Empleado",
                        onClick = { rol = "Empleado" }
                    )
                    Text(text = "Empleado")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón para Registrar la Empresa
                Button(onClick = {
                    val empresaData = hashMapOf(
                        "nombre_empresa" to nombreEmpresa,
                        "ciudad" to ciudad,
                        "cp" to cp,
                        "direccion" to direccion,
                        "estado" to estado,
                        "pais" to pais,
                        "rfc" to rfc
                    )

                    val userData = hashMapOf(
                        "username" to nombreUsuario,
                        "rol" to rol
                    )

                    val uid = authManager.getCurrentUser()?.uid  // Obtener el UID del usuario autenticado
                    uid?.let {
                        db.collection("empresa").document("datos_empresas")
                            .set(empresaData)
                            .addOnSuccessListener {
                                db.collection("users").document(uid)
                                    .update(userData as Map<String, Any>)
                                    .addOnSuccessListener {
                                        // Redirigir a home después del registro
                                        navController.navigate("home")
                                    }
                            }
                    }
                }) {
                    Text(text = "Registrar Empresa")
                }
            }
        }
    )
}
