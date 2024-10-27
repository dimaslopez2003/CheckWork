package com.example.checkwork.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.ai.client.generativeai.type.content

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3F51B5),
    secondary = Color(0xFF673AB7),
    background = Color(0xFF121212),
    onPrimary = Color.White,
    onSecondary = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0056E0),
    secondary = Color(0xFF0056E0),
    background = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.Black
)

@Composable
fun CheckWorkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,  // Tipografía personalizada si tienes alguna
        content = content
    )
}
