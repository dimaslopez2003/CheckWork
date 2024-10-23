import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.checkwork.Navigation.BottomNavigationBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CalendarView(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color(0xFF0056E0))

    val calendar = Calendar.getInstance()
    var month by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val days = getDaysInMonth(month, year)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    var selectedDay by remember { mutableStateOf(currentDay) }  // Día seleccionado

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {  // Navegar hacia atrás
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Cabecera del calendario
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {
                        if (month == 0) {
                            month = 11
                            year--
                        } else {
                            month--
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
                    }

                    Text(
                        text = "${getMonthName(month)} $year",
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    IconButton(onClick = {
                        if (month == 11) {
                            month = 0
                            year++
                        } else {
                            month++
                        }
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente")
                    }
                }

                // Días del calendario
                val firstDayOfWeek = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                }.get(Calendar.DAY_OF_WEEK) - 1

                for (row in 0 until 6) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0..6) {
                            val dayIndex = row * 7 + col - firstDayOfWeek
                            if (dayIndex >= 0 && dayIndex < days.size) {
                                val day = days[dayIndex]
                                ClickableText(
                                    text = AnnotatedString(day.toString()),
                                    style = TextStyle(
                                        color = when {
                                            day == currentDay && calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year -> Color.Red
                                            day == selectedDay -> Color.Blue  // Resaltar el día seleccionado
                                            else -> Color.Black
                                        },
                                        textAlign = TextAlign.Center
                                    ),
                                    onClick = {
                                        selectedDay = day  // Seleccionar el día
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f))
                            }
                        }
                    }
                }
            }
        }
    )
}

fun getDaysInMonth(month: Int, year: Int): List<Int> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MONTH, month)
    calendar.set(Calendar.YEAR, year)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return (1..daysInMonth).toList()
}

fun getMonthName(month: Int): String {
    return when (month) {
        0 -> "Enero"
        1 -> "Febrero"
        2 -> "Marzo"
        3 -> "Abril"
        4 -> "Mayo"
        5 -> "Junio"
        6 -> "Julio"
        7 -> "Agosto"
        8 -> "Septiembre"
        9 -> "Octubre"
        10 -> "Noviembre"
        11 -> "Diciembre"
        else -> ""
    }
}
