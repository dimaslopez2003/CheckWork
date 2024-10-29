package com.example.checkwork.Profile

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNum(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var selectedCountryCode by remember { mutableStateOf("+1") } // Código de país predeterminado
    var phoneNumber by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val countryCodes = listOf("+1" to "USA", "+52" to "MEX", "+44" to "UK", "+33" to "FR", "+49" to "DE", "+39" to "IT")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Phone Number", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0056E0)
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA))
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Enter your phone number",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0056E0)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                OutlinedButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier.width(100.dp)
                                ) {
                                    Text(text = selectedCountryCode)
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    countryCodes.forEach { code ->
                                        DropdownMenuItem(
                                            onClick = {
                                                selectedCountryCode = code.first
                                                expanded = false
                                            },
                                            text = { Text("${code.second} (${code.first})") }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (phoneNumber.isNotBlank()) {
                                    isSaving = true
                                    errorMessage = null
                                    successMessage = null
                                    savePhoneNumber(db, auth.currentUser?.uid, selectedCountryCode + phoneNumber) { success, error ->
                                        isSaving = false
                                        if (success) {
                                            successMessage = "Phone number saved successfully!"
                                        } else {
                                            errorMessage = error ?: "Failed to save phone number."
                                        }
                                    }
                                } else {
                                    errorMessage = "Please enter a valid phone number."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0056E0)),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        ) {
                            Text("Save", color = Color.White)
                        }

                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
                        }

                        errorMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = it, color = Color.Red)
                        }

                        successMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = it, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    )
}

private fun savePhoneNumber(
    db: FirebaseFirestore,
    userId: String?,
    phoneNumber: String,
    callback: (Boolean, String?) -> Unit
) {
    if (userId == null) {
        callback(false, "User not logged in.")
        return
    }

    db.collection("users").document(userId)
        .update("phoneNumber", phoneNumber.toLongOrNull())
        .addOnSuccessListener {
            callback(true, null)
        }
        .addOnFailureListener { e ->
            callback(false, e.message)
        }
}
