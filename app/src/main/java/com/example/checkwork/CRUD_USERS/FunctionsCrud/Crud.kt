package com.example.checkwork.CRUD_USERS.FunctionsCrud

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun updateEmployeeInFirebase(db: FirebaseFirestore, documentId: String, username: String, employeeId: String, departamento: String) {
    val updatedData = mapOf(
        "username" to username,
        "employeeId" to employeeId,
        "departamento" to departamento
    )
    db.collection("users").document(documentId).update(updatedData)
        .addOnSuccessListener {
            Log.d("Firestore", "Empleado actualizado exitosamente")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al actualizar el empleado", e)
        }
}

fun removeCompanyCodeFromEmployee(db: FirebaseFirestore, documentId: String, onUpdate: () -> Unit) {
    db.collection("users").document(documentId).update("company_code", null)
        .addOnSuccessListener {
            Log.d("Firestore", "Código de la empresa eliminado del empleado")
            onUpdate()
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al eliminar el código de la empresa", e)
        }
}

fun loadEmployees(db: FirebaseFirestore, companyCode: String, empleados: MutableList<Map<String, Any>>) {
    db.collection("users")
        .whereEqualTo("company_code", companyCode)
        .whereEqualTo("rol", "empleado")
        .get()
        .addOnSuccessListener { documents ->
            empleados.clear()
            for (document in documents) {
                val employeeData = document.data.toMutableMap()
                employeeData["documentId"] = document.id
                empleados.add(employeeData)
            }
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error al cargar los empleados", e)
        }
}

fun updateDarkModePreferenceInFirebase(auth: FirebaseAuth, db: FirebaseFirestore, isDarkMode: Boolean) {
    auth.currentUser?.uid?.let { userId ->
        db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
    }
}
