package com.dipolar.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NavyBlue = Color(0xFF1B3F8B)
val LightBlueBackground = Color(0xFFEEF2FF)
val CardWhite = Color(0xFFFFFFFF)
val CardLight = Color(0xFFF5F6FA)
val TextPrimary = Color(0xFF1A1A2E)
val TextSecondary = Color(0xFF6B7280)
val TagPink = Color(0xFFFFE4E8)
val TagPinkText = Color(0xFFE05C7A)
val ChipSelected = Color(0xFF1B3F8B)
val ChipUnselected = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE5FF),
    onPrimaryContainer = Color(0xFF001257),
    secondary = Color(0xFF5B6A8A),
    onSecondary = Color.White,
    background = LightBlueBackground,
    surface = CardWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun DipolarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
