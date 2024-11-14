package com.example.checkwork.form

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

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
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var nombreEmpresa by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var cp by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var rfc by remember { mutableStateOf("") }
    var cpError by remember { mutableStateOf(false) }
    var rfcError by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false
            }
        }
        delay(500) // Espera medio segundo antes de habilitar el botón de nuevo
        isBackButtonEnabled = true
    }

    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    fun generateCompanyCode(): String {
        return "COMP" + (10000..99999).random()
    }

    fun saveCompanyData(db: FirebaseFirestore, companyData: Map<String, Any>, userId: String) {
        val companyCode = generateCompanyCode()
        val companyDataWithCode = companyData.toMutableMap().apply {
            put("company_code", companyCode)
        }

        db.collection("Company_Code").document(companyCode)
            .set(companyDataWithCode)
            .addOnSuccessListener {
                Log.d("Firestore", "Company code saved successfully")

                db.collection("users").document(userId)
                    .update("company_code", companyCode)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Company code associated with user successfully")
                        showDialog = true // Mostrar el diálogo de éxito
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error associating company code with user", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error saving company code", e)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Registra tu empresa", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
                ),
                navigationIcon = {
                    IconButton(onClick =
                    {
                        if (isBackButtonEnabled){
                            isBackButtonEnabled = false
                            navController.popBackStack()
                        }
                    }){
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            updateDarkModePreferenceInFirebase(it)
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
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFF66ABE3)),
                color = if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFF66ABE3)
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
                            containerColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFFE0F7FA),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalance,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(8.dp),
                                tint = if (isDarkModeEnabled) Color.White else Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = nombreEmpresa,
                                onValueChange = { nombreEmpresa = it },
                                label = { Text("Nombre de la Empresa") },
                                textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.Black else Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    if (isDarkModeEnabled) Color.Black else Color.Black,
                                    if (isDarkModeEnabled) Color.Black else Color.Black
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

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
                                    textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                                    modifier = Modifier.weight(1f),
                                    isError = cpError,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                    colors = TextFieldDefaults.colors(
                                        if (isDarkModeEnabled) Color.White else Color.Black,
                                        if (isDarkModeEnabled) Color.LightGray else Color.Black
                                    )
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
                                    textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                                    modifier = Modifier.weight(1f),
                                    isError = rfcError,
                                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                    colors = TextFieldDefaults.colors(
                                        if (isDarkModeEnabled) Color.White else Color.Black,
                                        if (isDarkModeEnabled) Color.LightGray else Color.Black
                                    )
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = ciudad,
                                    onValueChange = { ciudad = it },
                                    label = { Text("Ciudad") },
                                    textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.colors(
                                        if (isDarkModeEnabled) Color.White else Color.Black,
                                        if (isDarkModeEnabled) Color.LightGray else Color.Black
                                    )
                                )

                                OutlinedTextField(
                                    value = estado,
                                    onValueChange = { estado = it },
                                    label = { Text("Estado") },
                                    textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                                    modifier = Modifier.weight(1f),
                                    colors = TextFieldDefaults.colors(
                                        if (isDarkModeEnabled) Color.White else Color.Black,
                                        if (isDarkModeEnabled) Color.LightGray else Color.Black
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = direccion,
                                onValueChange = { direccion = it },
                                label = { Text("Dirección") },
                                textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    if (isDarkModeEnabled) Color.White else Color.Black,
                                    if (isDarkModeEnabled) Color.LightGray else Color.Black
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = pais,
                                onValueChange = { pais = it },
                                label = { Text("País") },
                                textStyle = LocalTextStyle.current.copy(color = if (isDarkModeEnabled) Color.White else Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.colors(
                                    if (isDarkModeEnabled) Color.White else Color.Black,
                                    if (isDarkModeEnabled) Color.LightGray else Color.Black
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val empresaData = hashMapOf(
                                        "nombreEmpresa" to nombreEmpresa,
                                        "ciudad" to ciudad,
                                        "cp" to cp,
                                        "direccion" to direccion,
                                        "estado" to estado,
                                        "pais" to pais,
                                        "rfc" to rfc,
                                    )
                                    saveCompanyData(db, empresaData, auth.currentUser!!.uid)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDarkModeEnabled) Color(0xFF000000) else Color(0xFF0056E0),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Registrar Empresa")
                            }

                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = { Text(text = "Empresa Registrada") },
                                    text = { Text("La empresa ha sido registrada con éxito.") },
                                    confirmButton = {
                                        Button(onClick = {
                                            showDialog = false
                                            navController.navigate("home") {
                                                popUpTo("home") { inclusive = true }
                                            }
                                        }) {
                                            Text("Aceptar")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
