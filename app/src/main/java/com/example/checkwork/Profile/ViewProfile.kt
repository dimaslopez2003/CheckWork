package com.example.checkwork.Profile

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.checkwork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var employeeId by remember { mutableStateOf("") }
    var companyCode by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var fingerprintEnabled by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var storagePermissionGranted by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Solicitar permisos de cámara y almacenamiento
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
            storagePermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
        }
    )

    LaunchedEffect(Unit) {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )

        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    departamento = document.getString("departamento") ?: ""
                    username = document.getString("username") ?: ""
                    employeeId = document.getString("employeeId") ?: ""
                    role = document.getString("rol") ?: ""
                    profileImageUrl = document.getString("profileImageUrl")
                }

            if (role == "Administrador") {
                db.collection("empresa").document(userId).get()
                    .addOnSuccessListener { document ->
                        companyCode = document.getString("companyCode") ?: ""
                    }
            }
        }
    }

    // Lanza el intent para abrir la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
            uri?.let { uploadImageToFirebase(it) }
        }
    )

    // Lanza el intent para tomar una foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bmp ->
            bitmap = bmp
            bmp?.let { uploadImageToFirebaseFromBitmap(it) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.Logout, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    if (role == "Administrador") {
                        IconButton(onClick = { /* Lógica para compartir el código de empresa */ }) {
                            Icon(Icons.Filled.Share, contentDescription = "Compartir Código de Empresa", tint = Color.White)
                        }
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
                // Foto de perfil y botón de editar
                Box(contentAlignment = Alignment.TopEnd) {
                    if (profileImageUrl != null) {
                        Image(
                            painter = rememberImagePainter(profileImageUrl),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier.size(150.dp).clip(CircleShape)
                        )
                    } else if (bitmap != null) {
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier.size(150.dp).clip(CircleShape)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Foto de Perfil",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                        )
                    }

                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar Perfil", tint = Color(0xFF0056E0))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "DEPARTAMENTO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(text = departamento, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "USERNAME",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(text = username, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ID DE EMPLEADO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(text = employeeId, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Estado de la huella digital
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "INICIO DE SESIÓN CON HUELLA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (fingerprintEnabled) "ACTIVADO" else "DESACTIVADO",
                        fontSize = 16.sp,
                        color = if (fingerprintEnabled) Color.Green else Color.Red
                    )
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = "Estado de la Huella Digital",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (role == "Administrador") {
                    Text(
                        text = "CÓDIGO DE EMPRESA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = companyCode, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            // Lógica para compartir el código de empresa
                        }) {
                            Icon(Icons.Filled.Share, contentDescription = "Compartir Código de Empresa", tint = Color(0xFF0056E0))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        auth.signOut()
                        navController.navigate("login")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0056E0))
                ) {
                    Text("CERRAR SESIÓN", color = Color.White)
                }

                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Editar Foto de Perfil") },
                        text = {
                            Column {
                                Text("¿Deseas tomar una nueva foto o cargar una desde la galería?")
                            }
                        },
                        confirmButton = {
                            Row {
                                TextButton(onClick = {
                                    // Lógica para tomar una foto
                                    if (cameraPermissionGranted) {
                                        cameraLauncher.launch(null)
                                    } else {
                                        permissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA))
                                    }
                                    showDialog = false
                                }) {
                                    Text("Tomar Foto")
                                }
                                TextButton(onClick = {
                                    // Lógica para elegir una foto de la galería
                                    if (storagePermissionGranted) {
                                        galleryLauncher.launch("image/*")
                                    } else {
                                        permissionsLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
                                    }
                                    showDialog = false
                                }) {
                                    Text("Cargar desde Galería")
                                }
                            }
                        }
                    )
                }
            }
        }
    )
}

fun uploadImageToFirebase(uri: Uri) {
    val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/${FirebaseAuth.getInstance().currentUser?.uid}")
    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                saveImageUriToFirestore(downloadUri.toString())
            }
        }
        .addOnFailureListener {
            // Manejar error
        }
}

fun uploadImageToFirebaseFromBitmap(bitmap: Bitmap) {
    val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/${FirebaseAuth.getInstance().currentUser?.uid}")
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()

    storageRef.putBytes(data)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                saveImageUriToFirestore(downloadUri.toString())
            }
        }
        .addOnFailureListener {
            // Manejar error
        }
}

fun saveImageUriToFirestore(downloadUri: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("profileImageUrl", downloadUri)
    }
}
