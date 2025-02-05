package com.example.checkwork.CRUD_USERS

import com.example.checkwork.FunctionTime.db
import com.example.checkwork.NavigationRegister.dataentryes.CheckEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.perf.FirebasePerformance
import kotlinx.coroutines.tasks.await

data class EmployeeRecord(
    val fecha: String,
    val hora: String,
    val tipo: String,
    val latitud: String = null.toString(),
    val longitud: String = null.toString(),
    val googleMapsLink: String = generateGoogleMapsLink(latitud, longitud)
)

// Función para generar el enlace de Google Maps
fun generateGoogleMapsLink(latitud: String, longitud: String): String {
    return if (latitud.isNotEmpty() && longitud.isNotEmpty()) {
        "https://www.google.com/maps?q=$latitud,$longitud"
    } else {
        "Sin ubicación"
    }
}

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
                val longitud = document.getDouble("longitud")
                val latitud = document.getDouble("latitud")

                records.add(
                    EmployeeRecord(
                        fecha,
                        hora,
                        tipo,
                        latitud.toString(),
                        longitud.toString()
                    )
                )
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

suspend fun fetchCheckEntries(employeeId: String): List<CheckEntry> {
    val trace = FirebasePerformance.getInstance().newTrace("fetch_check_entries")
    trace.start()

    return try {
        val startTime = System.currentTimeMillis()
        val records = db.collection("checks")
            .whereEqualTo("employeeId", employeeId)
            .get()
            .await()
            .documents.mapNotNull { doc ->
                val fecha = doc.getString("fecha") ?: "N/A"
                val hora = doc.getString("hora") ?: "N/A"
                val tipo = doc.getString("tipo") ?: "N/A"
                val longitud = doc.getDouble("longitud") ?: 0.0
                val latitud = doc.getDouble("latitud") ?: 0.0
                CheckEntry(
                    fecha,
                    hora,
                    tipo,
                    longitud.toString(),
                    latitud.toString()
                )
            }
        trace.putMetric("fetch_time_ms", System.currentTimeMillis() - startTime)
        trace.incrementMetric("records_fetched", records.size.toLong())
        records
    } catch (e: Exception) {
        trace.putMetric("fetch_error", 1)
        emptyList()
    } finally {
        trace.stop()
    }
}
