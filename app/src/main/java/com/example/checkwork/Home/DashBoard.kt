package com.example.checkwork.Home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.checkwork.Checks.registrarEntradaSalida
import com.example.checkwork.FunctionTime.getCurrentTime
import com.example.checkwork.Navigation.BottomNavigationBar
import com.example.checkwork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var hasCheckedIn by remember { mutableStateOf(false) }
    var hasCheckedOut by remember { mutableStateOf(false) }
    var nombreEmpresa by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var companyCode by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    profileImageUrl = document.getString("profileImageUrl")
                    departamento = document.getString("departamento") ?: ""
                    username = document.getString("username") ?: ""
                    isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false
                    userRole = document.getString("rol") ?: ""
                    companyCode = document.getString("company_code") ?: ""

                    // Obtener el nombre de la empresa usando el companyCode
                    if (companyCode.isNotEmpty()) {
                        db.collection("Company_Code").document(companyCode).get()
                            .addOnSuccessListener { companyDoc ->
                                nombreEmpresa = companyDoc.getString("nombreEmpresa") ?: ""
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error al obtener el nombre de la empresa: ${e.message}")
                            }
                    }
                }
                .addOnFailureListener { Log.e("Firestore", "Error al obtener los datos del usuario") }
        }

        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }


    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    systemUiController.setSystemBarsColor(
        color = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (profileImageUrl != null) {
                            Image(
                                painter = rememberImagePainter(profileImageUrl),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                                    .clickable { navController.navigate("perfil") }
                            )
                        } else {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Imagen predeterminada",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp)
                            )
                        }
                        Switch(
                            checked = isDarkModeEnabled,
                            onCheckedChange = { isChecked ->
                                isDarkModeEnabled = isChecked
                                updateDarkModePreferenceInFirebase(isChecked)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                        )
                    }
                }
            )
        },
        drawerContent = {
            DrawerContent(navController, username, departamento, isDarkModeEnabled, userRole) {
                isDarkModeEnabled = it
                updateDarkModePreferenceInFirebase(it)
            }
        },
        content = {
            ContentSection(username, currentTime, nombreEmpresa, context, hasCheckedIn, hasCheckedOut, isDarkModeEnabled) {
                hasCheckedIn = it.first
                hasCheckedOut = it.second
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
fun DrawerContent(
    navController: NavHostController,
    username: String,
    departamento: String,
    isDarkModeEnabled: Boolean,
    userRole: String,
    onDarkModeToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF042159))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Text(
            text = departamento,
            style = MaterialTheme.typography.h6.copy(color = if (isDarkModeEnabled) Color.LightGray else Color.White),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = username,
            style = MaterialTheme.typography.body1.copy(color = if (isDarkModeEnabled) Color.LightGray else Color.White),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("perfil") }
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Perfil",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Perfil", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
                Text(
                    text = "Agrega una foto para identificarte.",
                    color = if (isDarkModeEnabled) Color.Gray else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = if (isDarkModeEnabled) Color.Gray else Color.White.copy(alpha = 0.3f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Brightness2,
                contentDescription = "Modo Oscuro",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Modo Oscuro", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
                Text(
                    text = "Para descansar tu vista activa modo oscuro.",
                    color = if (isDarkModeEnabled) Color.Gray else Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = isDarkModeEnabled,
                onCheckedChange = { onDarkModeToggle(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    if (userRole == "Administrador") {
                        navController.navigate("form")
                    } else {
                        navController.navigate("join_company")
                    }
                }
        ) {
            Icon(
                imageVector = if (userRole == "Administrador") Icons.Filled.Edit else Icons.Filled.GroupAdd,
                contentDescription = if (userRole == "Administrador") "Registrar Empresa" else "Unirme a Empresa",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = if (userRole == "Administrador") "Registrar Empresa" else "Unirme a Empresa",
                color = if (isDarkModeEnabled) Color.LightGray else Color.White,
                fontSize = 16.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("support") }
        ) {
            Icon(
                imageVector = Icons.Filled.Help,
                contentDescription = "Soporte y Asistencia",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Soporte y Asistencia", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("login") }
        ) {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Cerrar Sesión",
                tint = if (isDarkModeEnabled) Color.LightGray else Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Cerrar Sesión", color = if (isDarkModeEnabled) Color.LightGray else Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun ContentSection(
    username: String,
    currentTime: String,
    nombreEmpresa: String,
    context: android.content.Context,
    hasCheckedIn: Boolean,
    hasCheckedOut: Boolean,
    isDarkModeEnabled: Boolean,
    onCheckUpdate: (Pair<Boolean, Boolean>) -> Unit
) {
    var entryTime by remember { mutableStateOf<Long?>(null) } // Hora de entrada en milisegundos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFE0F7FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFF0F0F0)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Company: ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = nombreEmpresa,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFF0F0F0),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡BIENVENIDO!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFF0F0F0),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = currentTime, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            EntryExitButton(
                text = "Entrada",
                backgroundColor = Color(0xFF4CAF50),
                onClick = {
                    if (hasCheckedIn) {
                        showAlert(context, "Ya se ha registrado la entrada.")
                    } else {
                        val currentTimeMillis = System.currentTimeMillis()
                        entryTime = currentTimeMillis // Registrar hora de entrada

                        registrarEntradaSalida(
                            tipo = "entrada",
                            onSuccess = {
                                onCheckUpdate(Pair(true, hasCheckedOut))
                                Toast.makeText(context, "Check In exitoso", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onFailure = { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                }
            )
            EntryExitButton(
                text = "Salida",
                backgroundColor = Color(0xFFF44336),
                onClick = {
                    if (!hasCheckedIn) {
                        showAlert(
                            context,
                            "Debe registrar su entrada antes de registrar la salida."
                        )
                    } else if (hasCheckedOut) {
                        showAlert(context, "Ya se ha registrado la salida.")
                    } else {
                        val currentTimeMillis = System.currentTimeMillis()

                        // Validar si la salida ocurre dentro de las 8 horas
                        if (entryTime != null && (currentTimeMillis - entryTime!!) <= 8 * 60 * 60 * 1000) {
                            registrarEntradaSalida(
                                tipo = "salida",
                                onSuccess = {
                                    onCheckUpdate(Pair(hasCheckedIn, true))
                                    Toast.makeText(context, "Check Out exitoso", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onFailure = { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        } else {
                            showAlert(
                                context,
                                "La salida debe registrarse dentro de una jornada de 8 horas desde la entrada."
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun EntryExitButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Button(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor)
    ) {
        Text(text)
    }
}

fun showAlert(context: android.content.Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}