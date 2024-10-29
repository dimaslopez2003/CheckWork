package com.example.checkwork.Profile


import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
    var phoneNumber by remember { mutableStateOf<Long?>(null) }
    var fingerprintEnabled by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cameraPermissionGranted by remember { mutableStateOf(false) }
    var storagePermissionGranted by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

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
                    phoneNumber = document.getLong("phoneNumber")
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
                // Tarjeta de perfil
                Card(
                    backgroundColor = Color(0xFF0056E0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Foto de perfil
                        Box(contentAlignment = Alignment.BottomEnd) {
                            if (profileImageUrl != null) {
                                Image(
                                    painter = rememberImagePainter(profileImageUrl),
                                    contentDescription = "Foto de Perfil",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (bitmap != null) {
                                Image(
                                    bitmap = bitmap!!.asImageBitmap(),
                                    contentDescription = "Foto de Perfil",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = "Foto de Perfil",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                )
                            }

                            IconButton(
                                onClick = { showDialog = true },
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar Perfil", tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = departamento, fontSize = 16.sp, color = Color.White)
                        Text(text = username, fontSize = 14.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    InfoItem(icon = Icons.Filled.AssignmentInd, label = "ID DE EMPLEADO", value = employeeId)
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(icon = Icons.Filled.Fingerprint, label = "INICIO DE SESIÓN CON HUELLA", value = "", switchValue = fingerprintEnabled) {
                        fingerprintEnabled = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (role == "Administrador") {
                        InfoItem(icon = Icons.Filled.Diversity1, label = "CÓDIGO DE EMPRESA", value = companyCode)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoItem(icon = Icons.Filled.CoPresent, label = "ROL", value = role)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (phoneNumber != null) {
                        InfoItem(
                            icon = Icons.Filled.Call,
                            label = "Número Telefónico",
                            value = phoneNumber.toString(),
                            onClick = { showDeleteDialog = true }
                        )
                    } else {
                        InfoItem(
                            icon = Icons.Filled.Call,
                            label = "Agregar Número Telefónico",
                            value = "",
                            onClick = { navController.navigate("AddPhone") }
                        )
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

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Eliminar Número Telefónico") },
                        text = { Text("¿Estás seguro de que deseas eliminar tu número de teléfono?") },
                        confirmButton = {
                            TextButton(onClick = {
                                deletePhoneNumber { success ->
                                    if (success) {
                                        phoneNumber = null // Actualiza en la UI
                                    }
                                    showDeleteDialog = false
                                }
                            }) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    )
}

fun deletePhoneNumber(callback: (Boolean) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("phoneNumber", null)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    } else {
        callback(false)
    }
}
@Composable
fun InfoItem(icon: ImageVector, label: String, value: String, switchValue: Boolean? = null, onClick: (() -> Unit)? = null, onSwitchChange: ((Boolean) -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick?.invoke() }


    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            if (value.isNotEmpty()) {
                Text(text = value, fontSize = 14.sp, color = Color.Gray)
            }
        }
        if (switchValue != null && onSwitchChange != null) {
            Switch(checked = switchValue, onCheckedChange = onSwitchChange)
        }
    }
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
