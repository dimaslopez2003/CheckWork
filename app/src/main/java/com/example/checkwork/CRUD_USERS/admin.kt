package com.example.checkwork.CRUD_USERS

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminCrudScreen(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Campos de datos
    var userId by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var employeeId by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }

    // Mensajes de estado
    var statusMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Admin CRUD", fontSize = 24.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("User ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = employeeId,
            onValueChange = { employeeId = it },
            label = { Text("Employee ID") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = departamento,
            onValueChange = { departamento = it },
            label = { Text("Departamento") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botones CRUD
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                createUser(
                    db, userId, username, phoneNumber, employeeId, departamento
                ) { message -> statusMessage = message }
            }) {
                Text("Create")
            }

            Button(onClick = {
                readUser(db, userId) { userData, message ->
                    if (userData != null) {
                        username = userData["username"] as String? ?: ""
                        phoneNumber = (userData["phoneNumber"] as Long?)?.toString() ?: ""
                        employeeId = userData["employeeId"] as String? ?: ""
                        departamento = userData["departamento"] as String? ?: ""
                    }
                    statusMessage = message
                }
            }) {
                Text("Read")
            }

            Button(onClick = {
                updateUser(
                    db, userId, username, phoneNumber, employeeId, departamento
                ) { message -> statusMessage = message }
            }) {
                Text("Update")
            }

            Button(onClick = {
                deleteUser(db, userId) { message -> statusMessage = message }
            }) {
                Text("Delete")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (statusMessage.isNotEmpty()) {
            Text(
                text = statusMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

// Funciones de Firebase para CRUD
fun createUser(
    db: FirebaseFirestore,
    userId: String,
    username: String,
    phoneNumber: String,
    employeeId: String,
    departamento: String,
    callback: (String) -> Unit
) {
    val userMap = hashMapOf(
        "username" to username,
        "phoneNumber" to phoneNumber.toLongOrNull(),
        "employeeId" to employeeId,
        "departamento" to departamento
    )
    db.collection("users").document(userId).set(userMap)
        .addOnSuccessListener { callback("User created successfully") }
        .addOnFailureListener { e -> callback("Error creating user: ${e.message}") }
}

fun readUser(
    db: FirebaseFirestore,
    userId: String,
    callback: (Map<String, Any>?, String) -> Unit
) {
    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                callback(document.data, "User data retrieved successfully")
            } else {
                callback(null, "User not found")
            }
        }
        .addOnFailureListener { e -> callback(null, "Error reading user: ${e.message}") }
}

fun updateUser(
    db: FirebaseFirestore,
    userId: String,
    username: String,
    phoneNumber: String,
    employeeId: String,
    departamento: String,
    callback: (String) -> Unit
) {
    val userMap = mapOf(
        "username" to username,
        "phoneNumber" to phoneNumber.toLongOrNull(),
        "employeeId" to employeeId,
        "departamento" to departamento
    )
    db.collection("users").document(userId).update(userMap)
        .addOnSuccessListener { callback("User updated successfully") }
        .addOnFailureListener { e -> callback("Error updating user: ${e.message}") }
}

fun deleteUser(
    db: FirebaseFirestore,
    userId: String,
    callback: (String) -> Unit
) {
    db.collection("users").document(userId).delete()
        .addOnSuccessListener { callback("User deleted successfully") }
        .addOnFailureListener { e -> callback("Error deleting user: ${e.message}") }
}
