package com.example.checkwork.Checks

import com.example.checkwork.FunctionTime.getCurrentDate
import com.example.checkwork.FunctionTime.getCurrentTime
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun registrarEntradaSalida(
    tipo: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val userId = auth.currentUser?.uid

    if (userId != null) {
        // Obtener el documento correspondiente al usuario autenticado
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val documentId = document.id // ID del documento en la colección "users"
                val username = document.getString("username") ?: "Desconocido"

                val currentTimeMillis = System.currentTimeMillis()
                val uniqueId = "$documentId-$currentTimeMillis" // ID único combinado

                // Datos que se guardarán en "checks"
                val data = mapOf(
                    "employeeId" to documentId, // Aquí se guarda el ID del documento de "users"
                    "username" to username,
                    "tipo" to tipo,
                    "fecha" to getCurrentDate(),
                    "hora" to getCurrentTime(),
                    "comentarios" to "",
                )

                // Crear un nuevo documento en la colección "checks"
                db.collection("checks").document(uniqueId)
                    .set(data)
                    .addOnSuccessListener {
                        println("Registro $tipo exitoso para employeeId $documentId con ID único $uniqueId")
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        println("Error al registrar $tipo: ${e.message}")
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                println("Error al obtener datos del usuario: ${e.message}")
                onFailure(e)
            }
    } else {
        println("Usuario no autenticado")
        onFailure(Exception("Usuario no autenticado"))
    }
}
