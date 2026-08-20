package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
  primary = WhatsAppLightGreen,
  onPrimary = Color.White,
  primaryContainer = WhatsAppTopBarDark,
  onPrimaryContainer = WhatsAppLightGreen,
  secondary = WhatsAppEmerald,
  onSecondary = Color.White,
  background = WhatsAppBackgroundDark,
  onBackground = WhatsAppTextPrimaryDark,
  surface = WhatsAppSurfaceDark,
  onSurface = WhatsAppTextPrimaryDark,
  surfaceVariant = Color(0xFF202C33),
  onSurfaceVariant = WhatsAppTextSecondaryDark,
  outline = WhatsAppDividerDark
)

private val LightColorScheme = lightColorScheme(
  primary = WhatsAppGreen,
  onPrimary = Color.White,
  primaryContainer = WhatsAppTopBarLight,
  onPrimaryContainer = Color.White,
  secondary = WhatsAppLightGreen,
  onSecondary = Color.White,
  background = WhatsAppBackgroundLight,
  onBackground = WhatsAppTextPrimaryLight,
  surface = WhatsAppSurfaceLight,
  onSurface = WhatsAppTextPrimaryLight,
  surfaceVariant = Color(0xFFF0F2F5),
  onSurfaceVariant = WhatsAppTextSecondaryLight,
  outline = WhatsAppDividerLight
)

@Composable
fun WhatsAppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      val statusBarColor = if (darkTheme) WhatsAppTopBarDark else WhatsAppTopBarLight
      window.statusBarColor = statusBarColor.toArgb()
      val insetsController = WindowCompat.getInsetsController(window, view)
      insetsController.isAppearanceLightStatusBars = false
    }
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
