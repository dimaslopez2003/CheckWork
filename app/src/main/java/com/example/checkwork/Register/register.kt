import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.example.checkwork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var employeeId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Empleado") }
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val loadingAnimation by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.load))

    LaunchedEffect(Unit) {
        delay(500) // Habilita el botón de nuevo tras medio segundo
        isBackButtonEnabled = true
    }

    if (isLoading) {
        // Pantalla de animación de carga
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = loadingAnimation,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.size(150.dp)
            )
        }
    } else {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text("Registro", color = Color.White) },
                    backgroundColor = Color(0xFF0056E0),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (isBackButtonEnabled) {
                                    isBackButtonEnabled = false
                                    navController.popBackStack() // Volver atrás
                                }
                            }
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                        }
                    }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE0F7FA))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Crear una cuenta", fontSize = 24.sp, modifier = Modifier.padding(8.dp))

                    OutlinedTextField(
                        value = employeeId,
                        onValueChange = { employeeId = it },
                        label = { Text("ID de Empleado/No.Nómina") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Selecciona tu rol:", fontSize = 18.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                backgroundColor = Color(0xFF0056E0),
                                contentColor = Color.White
                            )
                        ) {
                            Text(selectedRole)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DropdownMenuItem(onClick = {
                                selectedRole = "Administrador"
                                expanded = false
                            }) {
                                Text("Administrador")
                            }
                            DropdownMenuItem(onClick = {
                                selectedRole = "Empleado"
                                expanded = false
                            }) {
                                Text("Empleado")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0056E0)),
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && employeeId.isNotEmpty()) {
                                isLoading = true
                                coroutineScope.launch {
                                    delay(3000) // Retraso para mostrar animación de carga
                                    try {
                                        auth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    val user = auth.currentUser
                                                    val userId = user?.uid

                                                    if (userId != null) {
                                                        val userMap = hashMapOf(
                                                            "username" to username,
                                                            "email" to email,
                                                            "employeeId" to employeeId,
                                                            "rol" to selectedRole
                                                        )
                                                        db.collection("users").document(userId).set(userMap)
                                                            .addOnSuccessListener {
                                                                isLoading = false
                                                                navController.navigate("login") {
                                                                    popUpTo("register") { inclusive = true }
                                                                }
                                                            }
                                                            .addOnFailureListener { e ->
                                                                isLoading = false
                                                                errorMessage = "Error al registrar: ${e.message}"
                                                            }
                                                    }
                                                } else {
                                                    isLoading = false
                                                    errorMessage = task.exception?.message ?: "Error desconocido"
                                                }
                                            }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = "Error: ${e.message}"
                                    }
                                }
                            } else {
                                errorMessage = "Todos los campos son obligatorios"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar", color = Color.White, fontSize = 14.sp)
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        )
    }
}
