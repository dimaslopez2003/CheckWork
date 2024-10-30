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
import com.example.checkwork.FunctionTime.getCurrentTime
import com.example.checkwork.Navigation.BottomNavigationBar
import com.example.checkwork.R
import com.example.checkwork.ui.theme.CheckWorkTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.checkwork.Checks.registrarEntradaSalida

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaPrincipal(navController: NavHostController, s: String) {

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFF0056E0))

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var username by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var currentTime by remember { mutableStateOf(getCurrentTime()) }
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var hasCheckedIn by remember { mutableStateOf(false) }
    var hasCheckedOut by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    profileImageUrl = document.getString("profileImageUrl")
                    departamento = document.getString("departamento") ?: ""
                    username = document.getString("username") ?: ""
                }
                .addOnFailureListener { Log.e("Firestore", "Error al obtener los datos") }
        }

        while (true) {
            delay(1000L)
            currentTime = getCurrentTime()
        }
    }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    CheckWorkTheme(darkTheme = isDarkModeEnabled) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("WorkCheckApp", color = Color.White) },
                    backgroundColor = Color(0xFF0056E0),
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { scaffoldState.drawerState.open() }
                        }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    actions = {
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
                    }
                )
            },
            drawerContent = {
                DrawerContent(navController, username, departamento, isDarkModeEnabled) {
                    isDarkModeEnabled = it
                }
            },
            content = {
                ContentSection(username, departamento, currentTime, context, hasCheckedIn, hasCheckedOut) {
                    hasCheckedIn = it.first
                    hasCheckedOut = it.second
                }
            },
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        )
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    username: String,
    departamento: String,
    isDarkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF042159))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),  // Assuming logo is stored in res/drawable/logo.png
            contentDescription = "Logo",
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Text(
            text = departamento,
            style = MaterialTheme.typography.h6.copy(color = Color.White),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = username,
            style = MaterialTheme.typography.body1.copy(color = Color.White),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { navController.navigate("perfil") }
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Perfil",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "Perfil", color = Color.White, fontSize = 16.sp)
                Text(
                    text = "Agrega una foto para identificarte.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = Color.White.copy(alpha = 0.3f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Brightness2,
                contentDescription = "Modo Oscuro",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Modo Oscuro", color = Color.White, fontSize = 16.sp)
                Text(
                    text = "Para descansar tu vista activa modo oscuro.",
                    color = Color.White.copy(alpha = 0.7f),
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
                .clickable { navController.navigate("form") }
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Registrar Empresa",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Registrar Empresa", color = Color.White, fontSize = 16.sp)
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
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Soporte y Asistencia", color = Color.White, fontSize = 16.sp)
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
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Cerrar Sesión", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun ContentSection(
    username: String,
    departamento: String,
    currentTime: String,
    context: android.content.Context,
    hasCheckedIn: Boolean,
    hasCheckedOut: Boolean,
    onCheckUpdate: (Pair<Boolean, Boolean>) -> Unit)
{
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
