package com.example.checkwork.Calendario

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioScreen(navController: NavHostController) {
    val currentMonth = remember { mutableStateOf(LocalDate.now()) }
    val selectedDate = remember { mutableStateOf<LocalDate?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Calendario",
            fontSize = 24.sp,
            color = Color(0xFF0056E0),
            modifier = Modifier.padding(16.dp)
        )

        // Mostrar el mes y los botones de navegación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = {
                coroutineScope.launch {
                    currentMonth.value = currentMonth.value.minusMonths(1)
                }
            }) {
                Text(text = "<", fontSize = 24.sp, color = Color(0xFF0056E0))
            }

            Text(
                text = currentMonth.value.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                fontSize = 18.sp,
                color = Color(0xFF0056E0)
            )

            IconButton(onClick = {
                coroutineScope.launch {
                    currentMonth.value = currentMonth.value.plusMonths(1)
                }
            }) {
                Text(text = ">", fontSize = 24.sp, color = Color(0xFF0056E0))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mostrar el calendario con los días del mes
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            val daysInMonth = getDaysInMonth(currentMonth.value)
            items(daysInMonth) { day ->
                DayItem(day = day, selectedDate = selectedDate.value) {
                    selectedDate.value = it
                    Toast.makeText(navController.context, "Fecha seleccionada: $it", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedDate.value?.let {
            Text(text = "Fecha seleccionada: ${it.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}", fontSize = 18.sp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayItem(day: LocalDate, selectedDate: LocalDate?, onClick: (LocalDate) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(if (day == selectedDate) Color(0xFF4CAF50) else Color.White)
            .padding(16.dp)
            .clickable {
                onClick(day)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            color = if (day == selectedDate) Color.White else Color.Black,
            fontSize = 18.sp
        )
    }
}

// Helper function to get the days of the current month
@RequiresApi(Build.VERSION_CODES.O)
fun getDaysInMonth(date: LocalDate): List<LocalDate> {
    val firstDayOfMonth = date.withDayOfMonth(1)
    val daysInMonth = mutableListOf<LocalDate>()
    val lastDay = date.lengthOfMonth()

    for (day in 1..lastDay) {
        daysInMonth.add(firstDayOfMonth.withDayOfMonth(day))
    }

    return daysInMonth
}
