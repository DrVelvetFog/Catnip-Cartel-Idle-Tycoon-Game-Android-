package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CartelDarkColorScheme = darkColorScheme(
  primary = PrimaryLight,
  onPrimary = OnPrimary,
  primaryContainer = PrimaryNeon,
  onPrimaryContainer = OnPrimaryContainer,
  secondary = SecondaryGold,
  onSecondary = OnSecondary,
  secondaryContainer = SecondaryGoldContainer,
  onSecondaryContainer = OnSecondaryContainer,
  error = ErrorRed,
  onError = OnError,
  errorContainer = ErrorContainer,
  onErrorContainer = OnErrorContainer,
  background = SurfaceDark,
  onBackground = OnSurface,
  surface = SurfaceDark,
  onSurface = OnSurface,
  surfaceVariant = SurfaceContainerHighest,
  onSurfaceVariant = OnSurfaceVariant,
  outline = Outline,
  outlineVariant = OutlineVariant,
  surfaceContainerLowest = SurfaceContainerLowest,
  surfaceContainerLow = SurfaceContainerLow,
  surfaceContainer = SurfaceContainer,
  surfaceContainerHigh = SurfaceContainerHigh,
  surfaceContainerHighest = SurfaceContainerHighest,
  surfaceDim = SurfaceDim,
  surfaceBright = SurfaceBright
)

@Composable
fun CatnipCartelTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = CartelDarkColorScheme,
    typography = Typography,
    content = content
  )
}
