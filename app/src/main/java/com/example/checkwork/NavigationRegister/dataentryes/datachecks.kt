package com.example.checkwork.NavigationRegister.dataentryes

data class CheckEntry(
    val fecha: String,
    val hora: String,
    val tipo: String,
    val comentarios: String = ""
)