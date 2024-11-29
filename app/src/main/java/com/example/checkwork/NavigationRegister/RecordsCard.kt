package com.example.checkwork.NavigationRegister

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkwork.NavigationRegister.dataentryes.CheckEntry
import com.example.checkwork.NavigationRegister.dataentryes.generatePdf
@Composable
fun RecordsCard(
    checkEntries: List<CheckEntry>,
    isDarkModeEnabled: Boolean,
    employeeId: String,
    departamento: String
) {
    val context = LocalContext.current
    val itemsPerPage = 10
    var currentPage by remember { mutableStateOf(0) }
    val totalPages = (checkEntries.size + itemsPerPage - 1) / itemsPerPage
    val pagedEntries = checkEntries.drop(currentPage * itemsPerPage).take(itemsPerPage)

    Column {
        // Botón para generar PDF
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                generatePdf(
                    context,
                    pagedEntries,
                    employeeId,
                    departamento,
                    nombreEmpresa = "Instituto Césare"
                )
            }) {
                Text(
                    text = "Generar PDF",
                    color = if (isDarkModeEnabled) Color.LightGray else Color.Black
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color.White
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TableHeader("Fecha", isDarkModeEnabled)
                    TableHeader("Hora", isDarkModeEnabled)
                    TableHeader("Tipo", isDarkModeEnabled)
                    TableHeader("Ubicación", isDarkModeEnabled)
                }
                Divider(color = if (isDarkModeEnabled) Color.Gray else Color.LightGray)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    pagedEntries.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TableCell(entry.fecha, isDarkModeEnabled)
                            TableCell(entry.hora, isDarkModeEnabled)
                            TableCell(entry.tipo, isDarkModeEnabled)
                            TableCellWithLink(entry.latitud, entry.longitud, isDarkModeEnabled)
                        }
                        Divider(color = if (isDarkModeEnabled) Color.Gray else Color.LightGray)
                    }
                }

                // Controles de paginación
                if (checkEntries.size > itemsPerPage) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { if (currentPage > 0) currentPage-- },
                            enabled = currentPage > 0
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Página anterior",
                                tint = if (isDarkModeEnabled) Color.LightGray else Color.Black
                            )
                        }
                        Text(
                            text = "${currentPage + 1} / $totalPages",
                            color = if (isDarkModeEnabled) Color.LightGray else Color.Black
                        )
                        IconButton(
                            onClick = { if (currentPage < totalPages - 1) currentPage++ },
                            enabled = currentPage < totalPages - 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Página siguiente",
                                tint = if (isDarkModeEnabled) Color.LightGray else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCellWithLink(
    latitud: String?,
    longitud: String?,
    isDarkModeEnabled: Boolean,
    textColor: Color = if (isDarkModeEnabled) Color.LightGray else Color.Black
) {
    val context = LocalContext.current

    val locationUrl = if (latitud != null && longitud != null) {
        "https://www.google.com/maps?q=${latitud},${longitud}"
    } else {
        null
    }

    Column {
        if (locationUrl != null) {
            Text(
                text = "Ver ubicación",
                color = Color.Blue,
                fontSize = 13.sp,
                modifier = Modifier.clickable {
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                            data = android.net.Uri.parse(locationUrl)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error al abrir el enlace", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            Text(
                text = "Sin ubicación",
                color = textColor,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun TableHeader(text: String, isDarkModeEnabled: Boolean) {
    Text(
        text = text,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        color = if (isDarkModeEnabled) Color.LightGray else Color.Black,
        fontSize = 16.sp
    )
}

@Composable
fun TableCell(text: String, isDarkModeEnabled: Boolean) {
    Text(
        text = text,
        color = if (isDarkModeEnabled) Color.LightGray else Color.Black,
        fontSize = 13.sp
    )
}
