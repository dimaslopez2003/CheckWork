package com.example.checkwork.FunctionTime

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

// Firestore instance
@SuppressLint("StaticFieldLeak")
val db = FirebaseFirestore.getInstance()

// Function to get the current time
fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return sdf.format(Date())
}

// Function to get the difference in hours between two times
fun calculateHoursDifference(startTime: String, endTime: String): Double {
    val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val startDate = sdf.parse(startTime)
    val endDate = sdf.parse(endTime)
    val differenceInMillis = endDate.time - startDate.time
    return (differenceInMillis / (1000 * 60 * 60)).toDouble() // Convert to hours
}

// Function to store check-in time in Firestore and show AlertDialog
@Composable
fun storeCheckIn(userId: String) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val currentTime = getCurrentTime()

    // Store check-in data in Firestore
    val checkData = hashMapOf(
        "entrada" to currentTime,
        "horas_faltantes" to "8 horas", // Jornada de 8 horas inicial
        "horas_trabajadas" to "0 horas",
        "salida" to "" // Inicialmente vacío
    )

    db.collection("checks")
        .document(userId)
        .collection("entradas_salidas")
        .add(checkData)
        .addOnSuccessListener {
            // Successfully stored check-in
            showDialog = true
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Error al guardar entrada: ${e.message}", Toast.LENGTH_SHORT).show()
        }

    // Mostrar AlertDialog después de hacer el check-in
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Entrada Registrada") },
            text = { Text("Hora de entrada: $currentTime") },
            confirmButton = {
                androidx.compose.material.Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

// Function to store check-out time, calculate hours worked, and show AlertDialog
@Composable
fun storeCheckOut(userId: String) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val currentTime = getCurrentTime()

    // Retrieve the latest check-in time from Firestore to calculate hours worked
    db.collection("checks")
        .document(userId)
        .collection("entradas_salidas")
        .orderBy("entrada", com.google.firebase.firestore.Query.Direction.DESCENDING)
        .limit(1)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val checkInTime = document.getString("entrada")
                if (checkInTime != null) {
                    // Calculate hours worked
                    val hoursWorked = calculateHoursDifference(checkInTime, currentTime)
                    val hoursRemaining = 8 - hoursWorked // Jornada de 8 horas

                    // Update the document with the check-out time and hours worked
                    val updateData = hashMapOf(
                        "salida" to currentTime,
                        "horas_trabajadas" to "$hoursWorked horas",
                        "horas_faltantes" to "${abs(hoursRemaining)} horas"
                    )

                    db.collection("checks")
                        .document(userId)
                        .collection("entradas_salidas")
                        .document(document.id)
                        .update(updateData as Map<String, Any>)
                        .addOnSuccessListener {
                            showDialog = true
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error al guardar salida: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

    // Mostrar AlertDialog con el resumen de horas trabajadas
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Salida Registrada") },
            text = {
                Text("Hora de salida: $currentTime\nHoras trabajadas: ${8 - abs(calculateHoursDifference(currentTime, getCurrentTime()))}")
            },
            confirmButton = {
                androidx.compose.material.Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
