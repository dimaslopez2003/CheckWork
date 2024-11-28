import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CalendarView(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val calendar = Calendar.getInstance()
    var month by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    val days = getDaysInMonth(month, year)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
    var isDarkModeEnabled by remember { mutableStateOf(false) }
    var isBackButtonEnabled by remember { mutableStateOf(true) }

    // Recuperar el estado de modo oscuro de Firebase
    LaunchedEffect(Unit) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    isDarkModeEnabled = document.getBoolean("darkModeEnabled") ?: false
                }
        }
    }
    LaunchedEffect(Unit) {
        delay(500) // Espera medio segundo antes de habilitar el botón de nuevo
        isBackButtonEnabled = true
    }

    // Guardar estado del modo oscuro en Firebase
    fun updateDarkModePreferenceInFirebase(isDarkMode: Boolean) {
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId).update("darkModeEnabled", isDarkMode)
        }
    }

    var selectedDay by remember { mutableStateOf(currentDay) }

    systemUiController.setSystemBarsColor(
        color = if (isDarkModeEnabled) Color(0xFF303030) else Color(0xFF0056E0)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    androidx.compose.material3.Text(
                        "Registra tu empresa",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick =
                    {
                        if (isBackButtonEnabled) {
                            isBackButtonEnabled = false
                            navController.popBackStack()
                        }
                    }) {
                        androidx.compose.material3.Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    androidx.compose.material3.Switch(
                        checked = isDarkModeEnabled,
                        onCheckedChange = {
                            isDarkModeEnabled = it
                            updateDarkModePreferenceInFirebase(it)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0056E0))
                    )
                },
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                isDarkModeEnabled = isDarkModeEnabled
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isDarkModeEnabled) Color(0xFF121212) else Color(0xFFE0F7FA)),
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior", tint = if (isDarkModeEnabled) Color.White else Color.Black)
                    }

                    Text(
                        text = "${getMonthName(month)} $year",
                        fontSize = 24.sp,
                        color = if (isDarkModeEnabled) Color.White else Color.Black,
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
                        Icon(Icons.Default.ArrowForward, contentDescription = "Mes siguiente", tint = if (isDarkModeEnabled) Color.White else Color.Black)
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
                                            day == selectedDay -> if (isDarkModeEnabled) Color.Cyan else Color.Blue
                                            else -> if (isDarkModeEnabled) Color.LightGray else Color.Black
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
