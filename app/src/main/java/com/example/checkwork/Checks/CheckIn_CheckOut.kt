package com.example.checkwork.Checks

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

fun registrarEntradaSalida(tipo: String, comentarios: String = "", onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    if (userId != null) {
        // Obtener detalles del usuario
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val employeeId = document.getString("employeeId") ?: "Desconocido"
                val username = document.getString("username") ?: "Desconocido"

                // Crear datos del check-in/check-out
                val fechaHoraActual = Date()
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

                val checkData = hashMapOf(
                    "employeeId" to employeeId,
                    "username" to username,
                    "fecha" to dateFormat.format(fechaHoraActual),
                    "hora" to timeFormat.format(fechaHoraActual),
                    "tipo" to tipo,
                    "comentarios" to comentarios
                )
                db.collection("checks").add(checkData)
                    .addOnSuccessListener {
                        Log.d("CheckIn_CheckOut", "Registro de $tipo exitoso para $username.")
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("CheckIn_CheckOut", "Error al registrar $tipo", exception)
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("CheckIn_CheckOut", "Error al obtener detalles del usuario", exception)
                onFailure(exception)
            }
    } else {
        Log.e("CheckIn_CheckOut", "Usuario no autenticado")
        onFailure(Exception("Usuario no autenticado"))
    }
}
