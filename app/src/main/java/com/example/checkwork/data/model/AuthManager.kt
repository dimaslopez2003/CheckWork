package com.example.checkwork.data.model

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Método para iniciar sesión con Google
    fun signInWithGoogle(
        googleSignInLauncher: ActivityResultLauncher<Intent>,
        context: Context
    ) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("659046565232-4odpm6no8p93psn3v2bsls629cague54.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    // Manejo del resultado de Google Sign-In
    fun handleGoogleSignInResult(
        task: Task<GoogleSignInAccount>,
        onResult: (Boolean, String?) -> Unit
    ) {
        try {
            val account = task.getResult(Exception::class.java)
            val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, signInTask.exception?.message)
                    }
                }
        } catch (e: Exception) {
            onResult(false, e.message)
        }
    }

    // Método para iniciar sesión con Email y Contraseña
    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Método para registrar un nuevo usuario con Email y Contraseña, y guardar sus datos en Firestore
    fun registerUserWithEmailAndPassword(
        email: String,
        password: String,
        username: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val uid = user.uid
                        val userData = hashMapOf(
                            "email" to email,
                            "username" to username,
                            "uid" to uid
                        )

                        // Guardar los datos del usuario en Firestore
                        firestore.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                onResult(true, null)
                            }
                            .addOnFailureListener { exception ->
                                onResult(false, exception.message)
                            }
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Obtener el usuario actual
    fun getCurrentUser() = auth.currentUser
}
