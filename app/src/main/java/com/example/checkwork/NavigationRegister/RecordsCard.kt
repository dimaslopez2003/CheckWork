package com.example.checkwork.NavigationRegister

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.checkwork.NavigationRegister.dataentryes.CheckEntry

@Composable
fun RecordsCard(checkEntries: List<CheckEntry>, isDarkModeEnabled: Boolean) {
    val itemsPerPage = 10
    var currentPage by remember { mutableStateOf(0) }
    var filterDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) } // Estado para controlar el DatePicker
    val totalPages = (checkEntries.size + itemsPerPage - 1) / itemsPerPage

    // Mostrar el DatePicker cuando `showDatePicker` sea true
    if (showDatePicker) {
        DatePickerDialog(onDateSelected = { selectedDate ->
            filterDate = selectedDate
            currentPage = 0 // Reiniciar la paginación al aplicar un filtro
            showDatePicker = false // Ocultar el DatePicker
        }, onDismissRequest = {
            showDatePicker = false // Ocultar el DatePicker si se cancela
        })
    }

    Column {
        // Filtro de fecha
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {
                showDatePicker = true // Mostrar el DatePicker al hacer clic
            }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtrar por fecha",
                    tint = if (isDarkModeEnabled) Color.LightGray else Color.Black
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
            backgroundColor = if (isDarkModeEnabled) Color(0xFF303030) else Color.White,
            elevation = 4.dp
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
                }
                Divider(color = if (isDarkModeEnabled) Color.Gray else Color.LightGray)

                // Aplicar filtro y paginación
                val filteredEntries = if (filterDate.isNotEmpty()) {
                    checkEntries.filter { it.fecha == filterDate }
                } else {
                    checkEntries
                }

                val pagedEntries = filteredEntries.drop(currentPage * itemsPerPage)
                    .take(itemsPerPage)

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
                        }
                        Divider(color = if (isDarkModeEnabled) Color.Gray else Color.LightGray)
                    }
                }

                // Controles de paginación
                if (filteredEntries.size > itemsPerPage) {
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