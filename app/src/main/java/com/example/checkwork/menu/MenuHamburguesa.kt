import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HamburgerMenu(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("WorkCheckApp", color = Color.White) },
                backgroundColor = Color(0xFF0056E0),
                navigationIcon = {
                    IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                }
            )
        },
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF042159))
                    .padding(16.dp)
            ) {
                Text(
                    text = "SISTEMAS",
                    style = MaterialTheme.typography.h6.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Dimas Arturo López Montalvo",
                    style = MaterialTheme.typography.body1.copy(color = Color.White),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Perfil", color = Color.White, fontSize = 16.sp)
                        Text(text = "Agrega una foto para identificarte.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                // Registrar biométricos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = "Registrar biométricos",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Registrar biométricos", color = Color.White, fontSize = 16.sp)
                        Text(text = "Habilitar Iniciar sesión con tu huella dactilar.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.3f))

                // Modo Oscuro
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Brightness2,
                        contentDescription = "Modo Oscuro",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Modo Oscuro", color = Color.White, fontSize = 16.sp)
                        Text(text = "Para descansar tu vista activa modo oscuro.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                // Asistencia
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Help,
                        contentDescription = "Asistencia",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Asistencia", color = Color.White, fontSize = 16.sp)
                        Text(text = "Soporte, ayuda y más.", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.3f))

                // Navegación al formulario
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            scope.launch { scaffoldState.drawerState.close() }
                            navController.navigate("form")
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Formulario",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Formulario", color = Color.White, fontSize = 16.sp)
                }

                // Cerrar sesión
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            scope.launch { scaffoldState.drawerState.close() }
                            navController.navigate("login")
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Cerrar Sesión", color = Color.White, fontSize = 16.sp)
                }
            }
        },
        content = { content() }
    )
}
