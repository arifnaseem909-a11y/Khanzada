package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PremiumGold,
    secondary = RoyalBlueLight,
    tertiary = PremiumGoldLight,
    background = AmoledBlack,
    surface = AmoledSurface,
    onPrimary = AmoledBlack,
    onSecondary = TextPrimaryDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = AmoledSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalBlue,
    secondary = PremiumGold,
    tertiary = RoyalBlueLight,
    background = TextPrimaryDark,
    surface = TextPrimaryDark,
    onPrimary = TextPrimaryLight,
    onSecondary = TextPrimaryLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Theme by default for the premium amoled luxury look
    dynamicColor: Boolean = false, // Disable dynamic system colors to keep our premium branding intact
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
