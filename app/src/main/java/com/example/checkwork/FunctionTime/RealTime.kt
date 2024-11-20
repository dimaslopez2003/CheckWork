package com.example.checkwork.FunctionTime

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// Firestore instance
@SuppressLint("StaticFieldLeak")
val db = FirebaseFirestore.getInstance()

// Function to get the current time
fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    return sdf.format(Date())
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date())
}


