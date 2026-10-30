package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkMode = compositionLocalOf { true }

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryBrandColor,
    secondary = InfoColor,
    tertiary = CreativePurple,
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = DangerColor,
    onError = Color.White
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryBrandColor,
    secondary = InfoColor,
    tertiary = CreativePurple,
    background = DarkBg,
    surface = CardBg,
    surfaceVariant = SurfaceColor,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderColor,
    outlineVariant = BorderSubtle,
    error = DangerColor,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colors = if (darkTheme) DarkColorScheme else LightColorScheme
  CompositionLocalProvider(LocalIsDarkMode provides darkTheme) {
    MaterialTheme(
      colorScheme = colors,
      typography = Typography,
      content = content
    )
  }
}
