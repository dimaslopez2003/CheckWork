package com.example.checkwork.NavigationRegister.dataentryes

data class CheckEntry(
    val fecha: String,
    val hora: String,
    val tipo: String,
    val latitud: String = null.toString(),
    val longitud: String = null.toString()
)