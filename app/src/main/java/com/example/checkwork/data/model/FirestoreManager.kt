package com.example.checkwork.data.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreManager {

    private val db = Firebase.firestore

    // Obtener el rol del usuario a partir de su UID
    fun getUserRole(uid: String, callback: (String?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("rol")
                    callback(role)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
