package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = DarkGold,
    onPrimary = DarkBackground,
    primaryContainer = DarkGreenContainer,
    onPrimaryContainer = DarkGold,
    secondary = IslamicGoldLight,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = IslamicGreenLight,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = IvorySurface,
    primaryContainer = IslamicGreenContainer,
    onPrimaryContainer = IslamicGreenOnContainer,
    secondary = IslamicGold,
    onSecondary = IvorySurface,
    secondaryContainer = IslamicGoldContainer,
    onSecondaryContainer = IslamicGoldOnContainer,
    tertiary = IslamicGoldDark,
    onTertiary = IvorySurface,
    background = IvoryBackground,
    onBackground = TextPrimary,
    surface = IvorySurface,
    onSurface = TextPrimary,
    surfaceVariant = IvorySurfaceVariant,
    onSurfaceVariant = TextSecondary
)

@Composable
fun ZadAlMuslimTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ZadAlMuslimTheme(darkTheme = darkTheme, content = content)
}
