package com.example.checkwork.Checks

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun solicitarPermisosUbicacion(activity: Activity, requestCode: Int) {
    val permisosRequeridos = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permisosNoConcedidos = permisosRequeridos.filter {
        ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
    }

    if (permisosNoConcedidos.isNotEmpty()) {
        ActivityCompat.requestPermissions(activity, permisosNoConcedidos.toTypedArray(), requestCode)
    }
}
