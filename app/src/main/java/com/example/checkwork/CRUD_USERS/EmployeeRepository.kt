package com.example.checkwork.CRUD_USERS

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class EmployeeRecord(
    val fecha: String,
    val hora: String,
    val tipo: String,
    val comentarios: String
)

class EmployeeRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getEmployeeRecords(employeeId: String): List<EmployeeRecord> {
        return try {
            val records = mutableListOf<EmployeeRecord>()
            val querySnapshot = db.collection("checks")
                .whereEqualTo("employeeId", employeeId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val fecha = document.getString("fecha") ?: ""
                val hora = document.getString("hora") ?: ""
                val tipo = document.getString("tipo") ?: ""
                val comentarios = document.getString("comentarios") ?: ""
                records.add(EmployeeRecord(fecha, hora, tipo, comentarios))
            }
            records
        } catch (e: Exception) {
            println("Error al obtener los registros del empleado: ${e.message}")
            emptyList()
        }
    }

    // Obtener el nombre del empleado
    suspend fun getEmployeeName(employeeId: String): String {
        return try {
            val document = db.collection("users").document(employeeId).get().await()
            document.getString("username") ?: "Empleado no encontrado"
        } catch (e: Exception) {
            println("Error al obtener el nombre del empleado: ${e.message}")
            "Empleado no encontrado"
        }
    }
}
