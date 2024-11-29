package com.example.checkwork.Home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkwork.Checks.registrarEntradaSalidaConUbicacion

@Composable
fun ContentSection(
    username: String,
    currentTime: String,
    nombreEmpresa: String,
    context: android.content.Context,
    hasCheckedIn: Boolean,
    hasCheckedOut: Boolean,
    isDarkModeEnabled: Boolean,
    onCheckUpdate: (Pair<Boolean, Boolean>) -> Unit
) {
    var entryTime by remember { mutableStateOf<Long?>(null) } // Hora de entrada en milisegundos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFE0F7FA)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFF0F0F0)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Company: ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = nombreEmpresa,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFF0F0F0),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡BIENVENIDO!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color(0xFFF0F0F0),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = currentTime, fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            EntryExitButton(
                text = "Entrada",
                backgroundColor = Color(0xFF4CAF50),
                onClick = {
                    if (hasCheckedIn) {
                        showAlert(context, "Ya se ha registrado la entrada.")
                    } else {
                        val currentTimeMillis = System.currentTimeMillis()
                        entryTime = currentTimeMillis // Registrar hora de entrada

                        registrarEntradaSalidaConUbicacion(
                            context = context, // Pasar el contexto requerido
                            tipo = "entrada",
                            onSuccess = {
                                onCheckUpdate(Pair(true, hasCheckedOut))
                                Toast.makeText(context, "Check In exitoso", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onFailure = { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }
                }
            )
            EntryExitButton(
                text = "Salida",
                backgroundColor = Color(0xFFF44336),
                onClick = {
                    if (!hasCheckedIn) {
                        showAlert(
                            context,
                            "Debe registrar su entrada antes de registrar la salida."
                        )
                    } else if (hasCheckedOut) {
                        showAlert(context, "Ya se ha registrado la salida.")
                    } else {
                        val currentTimeMillis = System.currentTimeMillis()

                        // Validar si la salida ocurre dentro de las 8 horas
                        if (entryTime != null && (currentTimeMillis - entryTime!!) <= 8 * 60 * 60 * 1000) {
                            registrarEntradaSalidaConUbicacion(
                                context = context, // Pasar el contexto requerido
                                tipo = "salida",
                                onSuccess = {
                                    onCheckUpdate(Pair(hasCheckedIn, true))
                                    Toast.makeText(context, "Check Out exitoso", Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onFailure = { e ->
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        } else {
                            showAlert(
                                context,
                                "La salida debe registrarse dentro de una jornada de 8 horas desde la entrada."
                            )
                        }
                    }
                }
            )
        }
    }
}
